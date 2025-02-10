package com.server.service.impl;

import com.server.dto.request.room.CreateRoomRequest;
import com.server.dto.request.room.FindRoomRequest;
import com.server.dto.request.room.UpdateRoomRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.room.RoomResponse;
import com.server.dto.response.user.UserResponse;
import com.server.dto.response.userteam.UserTeamResponse;
import com.server.entity.Game;
import com.server.entity.Match;
import com.server.entity.Room;
import com.server.entity.Team;
import com.server.entity.User;
import com.server.entity.UserTeam;
import com.server.enums.LevelRoomEnum;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import com.server.exceptions.BadRequestApiException;
import com.server.exceptions.MessageHandlingException;
import com.server.exceptions.NotFoundExceptionHandler;
import com.server.exceptions.RestApiException;
import com.server.repository.GameRepository;
import com.server.repository.MatchRepository;
import com.server.repository.MessageRepository;
import com.server.repository.RoomRepository;
import com.server.repository.TeamRepository;
import com.server.repository.UserRepository;
import com.server.repository.UserTeamRepository;
import com.server.service.MessageService;
import com.server.service.RoomService;
import com.server.util.RandomGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class RoomServiceImpl implements RoomService {

    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;
    private final TeamRepository teamRepository;
    private final EntityManager entityManager;
    private final MessageService messageService;
    private final MessageRepository messageRepository;
    private final MatchRepository matchRepository;

    /**
     * Tìm tất cả các phòng dựa trên các yêu cầu tìm kiếm.
     *
     * @param request Đối tượng chứa thông tin yêu cầu tìm kiếm.
     * @return PageableObject<RoomResponse> Danh sách các phòng được phân trang.
     */
    @Override
    public PageableObject<RoomResponse> findAll(FindRoomRequest request) {
        log.info("FindRoomRequest===> request = {}", request);
        if (StringUtils.isNotBlank(request.getInfoUser())) {
            Optional<User> optionalUser = userRepository.findByEmail(request.getInfoUser());
            if (optionalUser.isEmpty()) {
                throw new RestApiException("Bạn cần phải đăng nhập.");
            }
        }

        // Tạo Pageable từ thông tin trong FindGameRequest
        Pageable pageable = PageRequest.of(request.getPageNo(), request.getPageSize());

        StringBuilder sql = new StringBuilder("SELECT r FROM Room r Where 1 = 1 ");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(r) FROM Room r WHERE 1 = 1 ");

        if (StringUtils.isNotEmpty(request.getCodeGame())) {
            sql.append(" AND r.game.code LIKE :codeGame");
            countSql.append(" AND r.game.code LIKE :codeGame");
        }

        if (StringUtils.isNotEmpty(request.getCode())) {
            sql.append(" AND r.code LIKE :code");
            countSql.append(" AND r.code LIKE :code");
        }

        if (StringUtils.isNotEmpty(request.getName())) {
            sql.append(" AND r.name LIKE :name");
            countSql.append(" AND r.name LIKE :name");
        }

        if (ObjectUtils.isNotEmpty(request.getLevel())) {
            sql.append(" AND r.level = :level");
            countSql.append(" AND r.level = :level");
        }

        sql.append(" ORDER BY r.updatedDate DESC");

        // Thực hiện truy vấn và lấy kết quả
        TypedQuery<Room> query = entityManager.createQuery(sql.toString(), Room.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);

        // Thiết lập các tham số cho truy vấn
        if (StringUtils.isNotEmpty(request.getCodeGame())) {
            query.setParameter("codeGame", "%" + request.getCodeGame() + "%");
            countQuery.setParameter("codeGame", "%" + request.getCodeGame() + "%");
        }

        if (StringUtils.isNotEmpty(request.getCode())) {
            query.setParameter("code", "%" + request.getCode() + "%");
            countQuery.setParameter("code", "%" + request.getCode() + "%");
        }

        if (StringUtils.isNotEmpty(request.getName())) {
            query.setParameter("name", "%" + request.getName() + "%");
            countQuery.setParameter("name", "%" + request.getName() + "%");
        }

        if (ObjectUtils.isNotEmpty(request.getLevel())) {
            query.setParameter("level", request.getLevel());
            countQuery.setParameter("level", request.getLevel());
        }

        // Thiết lập phân trang cho truy vấn
        int pageNumber = request.getPageNo();
        int pageSize = request.getPageSize();
        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);

        // Lấy kết quả
        List<Room> rooms = query.getResultList();
        long total = countQuery.getSingleResult();

        // Chuyển đổi ProductDetail sang ProductDetailResponse
        List<RoomResponse> roomResponses = rooms.stream()
                .map(this::convertRoomResponse)
                .toList();

        // Tạo đối tượng Page và trả về
        Page<RoomResponse> page = new PageImpl<>(roomResponses, pageable, total);
        return new PageableObject<>(page);
    }

    /**
     * Thêm mới một phòng vào hệ thống và tạo hai đội liên quan đến phòng đó.
     *
     * @param dto Đối tượng chứa thông tin phòng cần thêm.
     * @return RoomResponse Thông tin phòng sau khi đã được thêm vào hệ thống.
     * @throws BadRequestApiException Nếu không tìm thấy .
     */
    @Override
    public RoomResponse add(@Valid CreateRoomRequest dto) {
        log.info("Starting to add room===> request = {}", dto.toString());

        // Kiểm tra game có tồn tại không
        Game game = gameRepository.findByCode(dto.getCodeGame())
                .orElseThrow(() -> new RestApiException("Game không tồn tại."));

        // Kiểm tra user có tồn tại không
        User user = userRepository.findByEmail(dto.getInfoUser())
                .orElseThrow(() -> new RestApiException("Người dùng không tồn tại."));

        // Xử lý điểm cược (pointBetLevel)
        int pointBetLevel = (dto.getPointBetLevel() != null) ? dto.getPointBetLevel() : 0;
        if (user.getPoint() < pointBetLevel) {
            throw new RestApiException("Người dùng không có đủ điểm để đặt cược.");
        }

        // Tạo room
        Room room = modelMapper.map(dto, Room.class);
        room.setPointBetLevel(pointBetLevel);
        room.setGame(game);
        room.setCode(generateUniqueRoomCode());
        room.setStatus(StatusEnum.ACTIVE);
        room.setLevel(ObjectUtils.isNotEmpty(dto.getLevel()) ? dto.getLevel() : LevelRoomEnum.SCRIMS);
        room.setNameUser(user.getName());

        // Tạo đội mặc định
        Team teamDefaultOne = new Team();
        teamDefaultOne.setRoom(room);
        teamDefaultOne.setName("TEAM A");
        teamDefaultOne.setDescription("TEAM A");

        Team teamDefaultTwo = new Team();
        teamDefaultTwo.setRoom(room);
        teamDefaultTwo.setName("TEAM B");
        teamDefaultTwo.setDescription("TEAM B");

        // Gán user vào team đầu tiên
        UserTeam userTeam = UserTeam.builder()
                .status(StatusEnum.ACTIVE)
                .user(user)
                .role(RoleEnum.ROLE_TEAM_LEADER)
                .team(teamDefaultOne)
                .build();
        teamDefaultOne.setUserTeam(userTeam);

        // Gán danh sách team vào room
        room.setTeams(Arrays.asList(teamDefaultOne, teamDefaultTwo));

        // Lưu room vào database
        Room savedRoom = roomRepository.save(room);
        RoomResponse response = convertRoomResponse(savedRoom);

        // Gửi thông báo qua WebSocket
        messageService.sendMessage("/subscribe/fetch-room-active/" + game.getCode(), response);

        return response;
    }



    /**
     * Cập nhật thông tin phòng.
     *
     * @param dto Đối tượng chứa thông tin phòng cần cập nhật.
     * @return RoomResponse Thông tin phòng sau khi cập nhật.
     */
    @Override
    public RoomResponse update(UpdateRoomRequest dto) {
        return null;
    }

    /**
     * Lấy thông tin phòng theo ID.
     *
     * @param id Mã ID của phòng.
     * @return RoomResponse Thông tin phòng.
     */
    @Override
    public RoomResponse getById(String id) {
        Optional<Room> optionalRoom = roomRepository.findById(id);
        if (optionalRoom.isEmpty()) {
            throw new NotFoundExceptionHandler("Không có phòng.");
        }

        return convertRoomResponse(optionalRoom.get());
    }

    /**
     * Sinh ra một mã phòng duy nhất.
     * Kiểm tra mã phòng sinh ra có tồn tại trong cơ sở dữ liệu hay không.
     * Nếu mã đã tồn tại, tiếp tục sinh mã mới.
     *
     * @return String Mã phòng duy nhất.
     */
    public String generateUniqueRoomCode() {
        String newRoomCode;
        do {
            newRoomCode = RandomGenerator.randomToString();
        } while (checkRoomCodeExists(newRoomCode));

        return newRoomCode;
    }

    /**
     * Kiểm tra xem mã phòng đã tồn tại trong cơ sở dữ liệu chưa.
     *
     * @param roomCode Mã phòng cần kiểm tra.
     * @return boolean Trả về true nếu mã phòng đã tồn tại, false nếu chưa tồn tại.
     */
    private boolean checkRoomCodeExists(String roomCode) {
        Optional<Room> optionalRoom = roomRepository.findRoomByCode(roomCode);
        return optionalRoom.isPresent();
    }

    /**
     * Chuyển đổi đối tượng Room thành RoomResponse.
     *
     * @param room Đối tượng phòng cần chuyển đổi.
     * @return RoomResponse Thông tin phòng dạng phản hồi.
     */
    private RoomResponse convertRoomResponse(Room room) {
        return modelMapper.map(room, RoomResponse.class);
    }


    /**
     * Lấy thông tin phòng theo Code.
     *
     * @param code Mã code của phòng.
     * @return RoomResponse Thông tin phòng.
     */
    @Override
    public RoomResponse findRoomByCode(String code) {
        Optional<Room> optionalRoom = roomRepository.findRoomByCode(code);
        if (optionalRoom.isEmpty()) {
            throw new NotFoundExceptionHandler("Không có phòng.");
        }

        return convertRoomResponse(optionalRoom.get());
    }

    @Override
    public RoomResponse removeRoom(String codeGame,String codeRoom, String name) {
        log.info("removeTeamRoom ==> codeRoom = {}, userId = {}", codeRoom, name);

        // Kiểm tra Room
        Room room = roomRepository.findRoomByCode(codeRoom)
                .orElseThrow(() -> new MessageHandlingException("Không có phòng."));

        // Kiểm tra quyền hạn: Chỉ Admin hoặc Room Owner mới được xóa Room
        boolean isAdmin = userRepository.findByEmail(name)
                .map(user -> user.getRole() == RoleEnum.ROLE_ADMIN)
                .orElse(false);

        if (!isAdmin && !room.getUserUpdate().equals(name)) {
            throw new MessageHandlingException("Bạn không có quyền xóa phòng này.");
        }

        log.info("removeTeamRoom ==> User is authorized, proceeding with deletion...");

        messageRepository.deleteAllByRoom_Code(codeRoom);
        userTeamRepository.deleteAllByTeam_Room_Code(codeRoom);

        // Lấy danh sách Team và xóa tất cả trận đấu liên quan
        List<Team> teamsInRoom = teamRepository.findByRoom_code(codeRoom);
        List<Match> matchesInTeam = teamsInRoom.stream()
                .flatMap(teamInRoom -> matchRepository.findAllByTeamWinOrTeamFaiId(teamInRoom.getId()).stream())
                .toList();

        if (!matchesInTeam.isEmpty()) {
            matchRepository.deleteAll(matchesInTeam);
        }

        teamRepository.deleteAll(teamsInRoom);
        roomRepository.deleteById(room.getId());

        log.info("removeTeamRoom ==> Room and related data deleted successfully.");

        RoomResponse response = convertRoomResponse(room);
        messageService.sendMessage("/subscribe/fetch-room-remove/" + codeGame, response);
        return response;
    }

}


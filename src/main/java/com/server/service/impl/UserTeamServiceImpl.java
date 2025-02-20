package com.server.service.impl;

import com.server.dto.request.userteam.CreateUserJoinTeamRequest;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.userteam.UserRoomGameResponse;
import com.server.dto.response.userteam.UserTeamResponse;
import com.server.entity.*;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import com.server.exceptions.MessageHandlingException;
import com.server.exceptions.NotFoundExceptionHandler;
import com.server.repository.MatchRepository;
import com.server.repository.MessageRepository;
import com.server.repository.RoomRepository;
import com.server.repository.TeamRepository;
import com.server.repository.UserRepository;
import com.server.repository.UserTeamRepository;
import com.server.service.UserTeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTeamServiceImpl implements UserTeamService {

    private final UserTeamRepository userTeamRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MatchRepository matchRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Lấy danh sách các thành viên trong một phòng (room).
     * <p>
     * Phương thức này tìm tất cả các thành viên trong đội nhóm thuộc phòng có mã codeRoom
     * và chuyển đổi chúng thành đối tượng UserTeamResponse.
     *
     * @param codeRoom Mã của phòng cần lấy danh sách thành viên.
     * @return List<UserTeamResponse> Danh sách các thành viên trong đội nhóm của phòng đó.
     */
    @Override
    public List<UserTeamResponse> findByRoom(String codeRoom, String status, String userId) {

        return userTeamRepository.findUserTeamByRoom(codeRoom, StatusEnum.valueOf(status))
                .stream()
                .map(this::convertUserTeamResponse)
                .toList();

    }

    @Override
    public List<UserTeamResponse> findByUserIdAndStatusActive(String userId) {
        return userTeamRepository.findUserTeamByUser_IdAndStatus(userId, StatusEnum.ACTIVE)
                .stream()
                .map(this::convertUserTeamResponse)
                .collect(Collectors.toList());
    }

    /**
     * Loại bỏ một thành viên khỏi đội nhóm.
     * <p>
     * Phương thức này xử lý logic để loại bỏ người dùng khỏi danh sách thành viên của đội nhóm.
     * Hiện tại, phương thức chỉ là một placeholder và mặc định trả về false.
     *
     * @param teamId ID của đội nhóm mà thành viên cần bị loại bỏ.
     * @param userId ID của người dùng cần bị loại khỏi đội nhóm.
     * @return UserTeamResponse Đối tượng UserTeamResponse tương ứng
     */
    @Override
    public UserTeamResponse kickMemberFromTeam(String teamId, String userId) {
        log.info("kickMemberFromTeam==> teamId = {}, userId = {}", teamId, userId);

        // Tìm Team
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new MessageHandlingException("Team không tồn tại"));

        // Kiểm tra User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MessageHandlingException("Người dùng không tồn tại."));

        // Tìm UserTeam
        UserTeam userTeam = userTeamRepository.findUserTeamByTeam_idAndUser_id(team.getId(), user.getId());
        if (userTeam == null) {
            log.info("kickMemberFromTeam==> User is not a member of this team.");
            throw new MessageHandlingException("Người dùng không phải là thành viên của nhóm này.");
        }

        // Kiểm tra nếu User bị kích là Team Leader
        boolean isTeamLeader = userTeam.getRole() == RoleEnum.ROLE_TEAM_LEADER;

        // Xóa User khỏi Team
        userTeamRepository.deleteById(userTeam.getId());

        // Nếu là Team Leader, chuyển quyền cho thành viên gần nhất
        if (isTeamLeader) {
            log.info("kickMemberFromTeam==> User was Team Leader, assigning new leader...");

            // Lấy danh sách các thành viên còn lại, sắp xếp theo thời gian tham gia
            List<UserTeam> remainingMembers = userTeamRepository.findByTeam_IdOrderByCreatedDateAsc(team.getId());
            if (ObjectUtils.isNotEmpty(remainingMembers)) {
                UserTeam newTeamLeader = remainingMembers.get(0);
                newTeamLeader.setRole(RoleEnum.ROLE_TEAM_LEADER);
                userTeamRepository.save(newTeamLeader);
                log.info("kickMemberFromTeam==> New Team Leader assigned: userId = {}",
                        newTeamLeader.getUser().getId());
            } else {
                log.info("kickMemberFromTeam==> No remaining members in team, no new leader assigned.");
            }
        }

        return convertUserTeamResponse(userTeam);
    }

    /**
     * Thêm một thành viên vào đ��i nhóm.
     * <p>
     * Phương thức này xử lý logic để thêm người dùng vào danh sách thành viên của đ��i nhóm.
     * Hiện tại, phương thức chỉ là một placeholder và mặc đ��nh trả về false.
     *
     * @param request Đối tượng user cần vào nhóm.
     * @return UserTeamResponse Đối tượng UserTeamResponse tương ứng.
     */
    @Override
    public UserTeamResponse processJoinTeamByRoom(CreateUserJoinTeamRequest request) {
        // Kiểm tra xem người dùng có tồn tại hay không trong hệ thống
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new MessageHandlingException("Người dùng không tồn tại."));

        // Lấy danh sách các nhóm trong phòng
        List<Team> teamsInRoom = teamRepository.findByRoom_code(request.getCodeRoom());
        if (ObjectUtils.isEmpty(teamsInRoom)) {
            throw new MessageHandlingException("Không có nhóm nào tồn tại");
        }

        Optional<UserTeam> userAlreadyInRoom = userTeamRepository.findUserTeamByTeam_Room_CodeAndUser_Id(request.getCodeRoom(), request.getUserId());
        if (userAlreadyInRoom.isPresent()) {
            log.info("processJoinTeamByRoom==> UserAlreadyInRoom = {}", userAlreadyInRoom.get());
            throw new MessageHandlingException("Người dùng đã tham gia một nhóm trong phòng này.");
        }

        // Sắp xếp các nhóm theo số lượng thành viên và chọn nhóm đầu tiên nếu có nhiều nhóm có cùng số lượng thành viên
        Team leastMemberTeam = teamsInRoom.stream()
                .min(Comparator.comparingInt(team -> getMemberCount(team.getId())))
                .orElseThrow(() -> new MessageHandlingException("Không có đội nào có thể tham gia."));

        // Kiểm tra xem nhóm đã có Team Leader chưa
        boolean hasTeamLeader = userTeamRepository.existsUserTeamByTeam_IdAndRole(leastMemberTeam.getId(),
                RoleEnum.ROLE_TEAM_LEADER);

        // Gán vai trò Team Leader nếu chưa có, nếu không gán vai trò Member
        RoleEnum assignedRole = hasTeamLeader ? RoleEnum.ROLE_MEMBER : RoleEnum.ROLE_TEAM_LEADER;

        // Thêm người dùng vào nhóm có ít thành viên nhất
        UserTeam userTeam = UserTeam.builder()
                .user(user)
                .team(leastMemberTeam)
                .role(assignedRole)
                .status(StatusEnum.PENDING)
                .build();

        return convertUserTeamResponse(userTeamRepository.save(userTeam));
    }


    /**
     * Cập nhật trạng thái của một UserTeam thành ACTIVE.
     * Trước khi cập nhật, kiểm tra xem người dùng đã tham gia phòng khác có trạng thái ACTIVE hay chưa.
     * Nếu người dùng đã có mặt trong phòng khác với trạng thái ACTIVE, sẽ ném ra ngoại lệ.
     *
     * @param id ID của UserTeam cần cập nhật.
     * @return UserTeamResponse Đối tượng UserTeam đã được cập nhật sau khi lưu vào repository.
     * @throws MessageHandlingException nếu không tìm thấy UserTeam hoặc nếu người dùng đã có mặt trong phòng khác với trạng thái ACTIVE.
     */
    @Override
    public UserTeamResponse updateStatus(String id) {
        Optional<UserTeam> userTeam = userTeamRepository.findById(id);
        if (userTeam.isEmpty()) {
            throw new MessageHandlingException("Người chơi không tồn tại.");
        }

        List<UserTeam> checkUserTeamInRoom = userTeamRepository.findUserTeamByUser_Id(userTeam.get().getUser().getId());
        if (ObjectUtils.isNotEmpty(checkUserTeamInRoom)) {
            checkUserTeamInRoom.stream()
                    .filter(item -> StatusEnum.ACTIVE.equals(item.getStatus()))
                    .findFirst()
                    .ifPresent(item -> {
                        throw new MessageHandlingException("Người dùng đã có mặt ở phòng khác.");
                    });
        }

        UserTeam userTeamEntity = userTeam.get();
        userTeamEntity.setStatus(StatusEnum.ACTIVE);

        UserTeamResponse response = convertUserTeamResponse(userTeamRepository.save(userTeamEntity));

        simpMessagingTemplate.convertAndSend("/subscribe/update-status-member-join-room-active/"
                + userTeam.get().getUser().getId(), new ResponseGlobal<>(response)
        );

        return response;
    }

    /**
     * Phương thức giúp người dùng rời khỏi phòng. Nếu người dùng là người quản lý hoặc người tạo phòng,
     * phương thức sẽ xóa tất cả tin nhắn, người dùng và đội trong phòng đó, và cuối cùng xóa phòng.
     *
     * @param codeRoom Mã phòng mà người dùng muốn rời khỏi.
     * @param userId   ID của người dùng muốn rời khỏi phòng.
     * @return UserTeamResponse Thông tin của UserTeam đã được xóa khỏi phòng.
     * @throws MessageHandlingException nếu có lỗi trong quá trình xóa dữ liệu liên quan đến phòng.
     */
    @Override
    @Transactional
    public UserTeamResponse exitTeamRoom(String codeRoom, String userId) {
        log.info("ExitTeamRoom==> codeRoom = {}, userId = {}", codeRoom, userId);

        // Kiểm tra Room
        Room room = roomRepository.findRoomByCode(codeRoom)
                .orElseThrow(() -> new MessageHandlingException("Room không tồn tại."));

        // Kiểm tra UserTeam
        UserTeam userTeam = userTeamRepository.findUserTeamByTeam_Room_CodeAndUser_Id(codeRoom, userId)
                .orElseThrow(() -> new MessageHandlingException("Team không tồn tại."));

        // Lấy thông tin Team và kiểm tra vai trò của User
        Team team = userTeam.getTeam();
        log.info("exitTeamRoom==> Team = {}", userTeam.getId());
        boolean isTeamLeader = userTeam.getRole() == RoleEnum.ROLE_TEAM_LEADER;

        // Xóa User khỏi Team
        userTeamRepository.deleteById(userTeam.getId());

        // Nếu là Room Owner, xóa toàn bộ room và các liên quan
        if (room.getUserCreate().equals(userId)) {
            log.info("exitTeamRoom==> Exit All team ");
            List<Message> messagesInRoom = messageRepository.findMessageByRoom_codeOrderByUpdatedDateAsc(codeRoom);
            messagesInRoom.forEach(message -> messageRepository.deleteById(message.getId()));

            List<UserTeam> userTeamsInRoom = userTeamRepository.findUserTeamByTeam_Room_Code(codeRoom);
            userTeamsInRoom.forEach(item -> userTeamRepository.deleteById(item.getId()));

            List<Team> teamsInRoom = teamRepository.findByRoom_code(codeRoom);
            List<Match> matchesInTeam = teamsInRoom.stream()
                    .flatMap(teamInRoom -> matchRepository.findAllByTeamWinOrTeamFaiId(teamInRoom.getId()).stream())
                    .toList();

            if (!matchesInTeam.isEmpty()) {
                log.info("exitTeamRoom==> Delete all matches in team");
                matchRepository.deleteAll(matchesInTeam);
                matchesInTeam.forEach(item -> matchRepository.deleteById(item.getId()));
            }

            teamsInRoom.forEach(item -> teamRepository.deleteById(item.getId()));
            roomRepository.deleteById(room.getId());
        }

        if (isTeamLeader) {
            // Nếu là Team Leader, chuyển quyền Team Leader cho người vào gần nhất
            log.info("exitTeamRoom==> User was Team Leader, assigning new leader...");
            List<UserTeam> membersInTeam = userTeamRepository.findByTeam_IdOrderByCreatedDateAsc(team.getId());
            if (!membersInTeam.isEmpty()) {
                UserTeam newTeamLeader = membersInTeam.get(0); // Người vào nhóm đầu tiên (sớm nhất)
                newTeamLeader.setRole(RoleEnum.ROLE_TEAM_LEADER);
                userTeamRepository.save(newTeamLeader);
                log.info("exitTeamRoom==> New Team Leader assigned: userId = {}", newTeamLeader.getUser().getId());
            }
        }

        return convertUserTeamResponse(userTeam);
    }

    /**
     * Xóa một người dùng khỏi nhóm dựa trên ID của UserTeam.
     *
     * @param userTeamId ID của UserTeam cần xóa.
     * @return UserTeamResponse Trả về thông tin của UserTeam đã bị xóa.
     * @throws NotFoundExceptionHandler nếu không tìm thấy UserTeam với ID được cung cấp.
     */
    @Override
    public UserTeamResponse removeUserFromTeam(String userTeamId) {
        Optional<UserTeam> userTeam = userTeamRepository.findById(userTeamId);
        if (userTeam.isEmpty()) {
            throw new NotFoundExceptionHandler("Người chơi không tồn tại.");
        }

        userTeamRepository.deleteById(userTeam.get().getId());
        return convertUserTeamResponse(userTeam.get());
    }

    @Override
    public UserRoomGameResponse findRoomCodeAndGameCodeByUserId(String userId) {
        Optional<UserTeam> userTeamOp = userTeamRepository.findByUser_Id(userId);
        if (userTeamOp.isEmpty()) {
            return new  UserRoomGameResponse(null, null);
        }

        UserTeam userTeam = userTeamOp.get();
        Room room = userTeam.getTeam().getRoom();
        Game game = room.getGame();
        return new UserRoomGameResponse(room.getCode(), game.getCode());
    }


    /**
     * Chuyển đổi đối tượng UserTeam thành UserTeamResponse để trả về trong API.
     *
     * @param userTeam Đối tượng UserTeam cần chuyển đổi.
     * @return UserTeamResponse Đối tượng UserTeamResponse tương ứng.
     */
    private UserTeamResponse convertUserTeamResponse(UserTeam userTeam) {
        return modelMapper.map(userTeam, UserTeamResponse.class);
    }

    private int getMemberCount(String teamId) {
        return userTeamRepository.countUserTeamByTeam_id(teamId);
    }

}

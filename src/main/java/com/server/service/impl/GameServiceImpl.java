package com.server.service.impl;

import com.server.dto.request.game.CreateGameRequest;
import com.server.dto.request.game.FindGameRequest;
import com.server.dto.request.game.UpdateGameRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.game.GameResponse;
import com.server.entity.Game;
import com.server.enums.DisplayEnum;
import com.server.enums.StatusEnum;
import com.server.exceptions.BadRequestApiException;
import com.server.exceptions.RestApiException;
import com.server.repository.GameRepository;
import com.server.service.GameService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final ModelMapper modelMapper;
    private final GameRepository gameRepository;
    private final EntityManager entityManager;

    /**
     * Tìm kiếm danh sách game dựa trên điều kiện tìm kiếm.
     *
     * @param request Đối tượng chứa các điều kiện tìm kiếm (phân trang, lọc theo code, tên,...).
     * @return Đối tượng phân trang chứa danh sách game và thông tin phân trang.
     */
    @Override
    public PageableObject<GameResponse> findAll(FindGameRequest request) {
        // Tạo Pageable từ thông tin trong FindGameRequest
        Pageable pageable = PageRequest.of(request.getPageNo(), request.getPageSize());

        StringBuilder sql = new StringBuilder("SELECT g FROM Game g Where 1 = 1 ");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(g) FROM Game g WHERE 1 = 1 ");

        if (StringUtils.isNotEmpty(request.getCode())) {
            sql.append(" AND g.code LIKE :code");
            countSql.append(" AND g.code LIKE :code");
        }
        if (StringUtils.isNotEmpty(request.getName())) {
            sql.append(" AND g.name LIKE :name");
            countSql.append(" AND g.name LIKE :name");
        }
        sql.append(" ORDER BY g.updatedDate DESC");

        // Thực hiện truy vấn và lấy kết quả
        TypedQuery<Game> query = entityManager.createQuery(sql.toString(), Game.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);

        // Thiết lập các tham số cho truy vấn
        if (StringUtils.isNotEmpty(request.getCode())) {
            query.setParameter("code", "%" + request.getCode() + "%");
            countQuery.setParameter("code", "%" + request.getCode() + "%");
        }
        if (StringUtils.isNotEmpty(request.getName())) {
            query.setParameter("name", "%" + request.getName() + "%");
            countQuery.setParameter("name", "%" + request.getName() + "%");
        }

        // Thiết lập phân trang cho truy vấn
        int pageNumber = request.getPageNo();
        int pageSize = request.getPageSize();
        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);

        // Lấy kết quả
        List<Game> games = query.getResultList();
        long total = countQuery.getSingleResult();

        // Chuyển đổi ProductDetail sang ProductDetailResponse
        List<GameResponse> gameResponses = games.stream()
                .map(this::convertGameResponse)
                .toList();

        // Tạo đối tượng Page và trả về
        Page<GameResponse> page = new PageImpl<>(gameResponses, pageable, total);
        return new PageableObject<>(page);

    }

    /**
     * Thêm mới một game vào hệ thống.
     *
     * @param dto Đối tượng chứa thông tin game cần thêm mới.
     * @return Đối tượng phản hồi chứa thông tin game sau khi thêm.
     * @throws RestApiException Nếu mã game đã tồn tại trong hệ thống.
     */
    @Override
    public GameResponse add(CreateGameRequest dto) {
        // Kiểm tra xem mã game có tồn tại hay không
        Optional<Game> optionalGame = gameRepository.findByCode(dto.getCode());
        if (optionalGame.isPresent()) {
            log.info("===> Stop add game: {}", optionalGame.get());
            throw new RestApiException("Mã game ");
        }

        // Chuyển đổi DTO thành entity
        Game game = modelMapper.map(dto, Game.class);
        game.setStatus(StatusEnum.ACTIVE);
        game.setDisplay(DisplayEnum.VISIBLE);
        gameRepository.save(game);

        return convertGameResponse(game);
    }

    /**
     * Cập nhật thông tin của một game hiện có.
     *
     * @param dto Đối tượng chứa thông tin game cần cập nhật.
     * @return Đối tượng phản hồi chứa thông tin game sau khi cập nhật.
     */
    @Override
    public GameResponse update(UpdateGameRequest dto) {
        return null;
    }

    /**
     * Lấy thông tin chi tiết của một game dựa trên ID.
     *
     * @param id ID của game cần tìm.
     * @return Đối tượng phản hồi chứa thông tin chi tiết của game.
     * @throws BadRequestApiException Nếu không tìm thấy game với ID đã cho.
     */
    @Override
    public GameResponse getById(String id) {
        Optional<Game> optionalGame = gameRepository.findByCode(id);
        if (optionalGame.isEmpty()) {
            throw new BadRequestApiException("Game không tồn tại");
        }

        return convertGameResponse(optionalGame.get());
    }

    /**
     * Chuyển đổi một đối tượng Game entity thành GameResponse DTO.
     *
     * @param game Đối tượng Game entity.
     * @return Đối tượng phản hồi GameResponse DTO.
     */
    private GameResponse convertGameResponse(Game game) {
        return modelMapper.map(game, GameResponse.class);
    }

    @Override
    public GameResponse findByCodeGame(String codeGame) {
        Optional<Game> optionalGame = gameRepository.findByCode(codeGame);
        if (optionalGame.isEmpty()) {
            throw new BadRequestApiException("Game không tồn tại");
        }

        return convertGameResponse(optionalGame.get());
    }
}

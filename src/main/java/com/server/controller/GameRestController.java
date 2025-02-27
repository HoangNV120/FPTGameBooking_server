package com.server.controller;

import com.server.dto.request.game.CreateGameRequest;
import com.server.dto.request.game.FindGameRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.game.GameResponse;
import com.server.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameRestController {

    private final GameService gameService;

    /**
     * Tìm kiếm danh sách game theo điều kiện.
     *
     * @param request Đối tượng chứa các tiêu chí tìm kiếm game (ví dụ: phân trang, lọc theo code, tên,...).
     * @return Đối tượng phản hồi toàn cục chứa danh sách game và thông tin phân trang.
     */
    @PostMapping("/search")
    public ResponseGlobal<PageableObject<GameResponse>> view(@RequestBody FindGameRequest request) {
        log.info("Request find==> request = {}", request.toString());
        return new ResponseGlobal<>(gameService.findAll(request));
    }

    /**
     * Thêm một game mới vào hệ thống.
     *
     * @param request Đối tượng chứa thông tin game cần thêm mới (được xác thực bằng @Valid).
     * @return Đối tượng phản hồi toàn cục chứa thông tin game sau khi thêm thành công.
     */
    @PostMapping
    public ResponseGlobal<GameResponse> add(@Valid @RequestBody CreateGameRequest request) {
        log.info("Create game====> request = {}", request.toString());
        return new ResponseGlobal<>(gameService.add(request));
    }

    @GetMapping("/find-by-code-game")
    public ResponseGlobal<GameResponse> findByCode(@RequestParam("codeGame") String codeGame) {
        log.info("Find By code game====> codeGame = {}", codeGame);
        return new ResponseGlobal<>(gameService.findByCodeGame(codeGame));
    }
}


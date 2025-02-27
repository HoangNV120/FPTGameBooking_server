package com.server.service;

import com.server.dto.request.game.CreateGameRequest;
import com.server.dto.request.game.FindGameRequest;
import com.server.dto.request.game.UpdateGameRequest;
import com.server.dto.response.game.GameResponse;
import com.server.service.common.BaseService;

public interface GameService extends BaseService<GameResponse, CreateGameRequest,
        UpdateGameRequest, FindGameRequest> {
    GameResponse findByCodeGame(String codeGame);
}

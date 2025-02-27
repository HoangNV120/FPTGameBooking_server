package com.server.controller;

import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.team.TeamResponse;
import com.server.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/team")
@RequiredArgsConstructor
public class TeamRestController {

    private final TeamService teamService;

    /**
     * Lấy danh sách team theo code room.
     *
     * @param codeRoom chứa các tham số room.
     * @return List<TeamResponse> chứa danh sách Team theo codeRoom.
     */
    @PostMapping("/find-team-by-code-room")
    public ResponseGlobal<List<TeamResponse>> findTeamByCodeRoom(@RequestParam("codeRoom") String codeRoom) {
        log.info("FindTeamByCodeRoom: codeRoom ={}", codeRoom);

        return new ResponseGlobal<>(teamService.findAllTeamsByCodeRoom(codeRoom));
    }


}

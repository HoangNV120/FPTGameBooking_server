package com.server.service.impl;

import com.server.dto.request.match.MatchRequest;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.match.MatchResponse;
import com.server.dto.response.match.ResultMatchResponse;
import com.server.dto.response.user.UserResponse;
import com.server.entity.*;
import com.server.enums.MatchStatusEnum;
import com.server.enums.RoleEnum;
import com.server.exceptions.NotFoundExceptionHandler;
import com.server.repository.*;
import com.server.service.MatchService;
import com.server.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final ModelMapper modelMapper;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final RoomRepository roomRepository;
    private final UserTeamRepository userTeamRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    /**
     * Thêm kết quả trận đấu cho một giải đấu.
     *
     * @param request Thông tin yêu cầu thêm kết quả trận đấu, bao gồm thông tin đội thắng, đội thua, và mô tả trận đấu.
     * @return MatchResponse Phản hồi chứa thông tin trận đấu vừa được thêm.
     * @throws NotFoundExceptionHandler Nếu không tìm thấy phòng hoặc đội trong hệ thống.
     */
    @Override
    public MatchResponse addMatchTeamWin(MatchRequest request) {
        log.info("Starting to add match===> request = {}", request.toString());

        // Lấy phòng theo mã phòng từ yêu cầu
        Room room = roomRepository.findRoomByCode(request.getCodeRoom())
                .orElseThrow(() -> new NotFoundExceptionHandler("Không tìm thấy phòng"));

        // Lấy danh sách các đội trong phòng
        List<Team> teamInRoom = teamRepository.findByRoom_code(room.getCode());

        // Lấy thông tin đội thắng theo ID
        Team teamWin = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new NotFoundExceptionHandler("Không tìm thấy đội thắng cuộc trong phòng."));

        // Tìm đội thua
        Optional<Team> teamLose = teamInRoom.stream()
                .filter(item -> !item.getId().equals(teamWin.getId()))
                .findFirst();

        if (teamLose.isEmpty()) {
            throw new NotFoundExceptionHandler("Không tìm thấy đội thua cuộc trong phòng.");
        }


        // Kiểm tra quyền của người dùng có phải là Lead không
        Optional<UserTeam> hasTeamLeaderWin = userTeamRepository
                .findUserTeamByTeam_Room_IdAndUser_IdAndRole(room.getId(), request.getUserId(),
                        RoleEnum.ROLE_TEAM_LEADER);
        if (hasTeamLeaderWin.isEmpty()) {
            throw new NotFoundExceptionHandler("Chỉ có trưởng nhóm mới có thể thêm kết quả trận đấu.");
        }

        // Lấy 2 đội trưởng của 2 đội
        UserTeam teamWinLeader = userTeamRepository
                .findUserTeamByTeam_IdAndRole(teamWin.getId(), RoleEnum.ROLE_TEAM_LEADER)
                .orElseThrow(() -> new NotFoundExceptionHandler("Không tìm thấy đội trưởng đội thắng"));

        UserTeam teamLoseLeader = userTeamRepository
                .findUserTeamByTeam_IdAndRole(teamLose.get().getId(), RoleEnum.ROLE_TEAM_LEADER)
                .orElseThrow(() -> new NotFoundExceptionHandler("Không tìm thấy đội trưởng đội thua"));


        // Tạo và lưu thông tin trận đấu
        Match match = new Match();
        match.setDescription(request.getDescription());
        match.setLinkVideo(request.getLinkVideo());
        match.setTeamWin(teamWin);
        match.setTeamFail(teamLose.get());
        match.setMatchStatus(MatchStatusEnum.PENDING);

        MatchResponse response = convertMatchResponse(matchRepository.save(match));

        // Gửi thông báo qua WebSocket đến đội trưởng của teamLose
        simpMessagingTemplate.convertAndSend(
                "/subscribe/confirm-match-active/"
                        + room.getCode() + "/" + (teamWinLeader.getUser().getId().equals(request.getUserId())
                        ? teamLoseLeader.getUser().getId() : teamWinLeader.getUser().getId()),
                new ResponseGlobal<>(response)
        );

        return response;
    }


    @Override
    public MatchResponse updateMatch(MatchRequest request) {
        log.info("Starting to update match===> request = {}", request.toString());
        Room room = roomRepository.findRoomByCode(request.getCodeRoom())
                .orElseThrow(() -> new NotFoundExceptionHandler("Không tìm thấy phòng"));

        Team teamLose = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new NotFoundExceptionHandler("Đội thua không tìm thấy"));

        Match match = matchRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundExceptionHandler("Không tìm thấy kết quả phù hợp"));

        match.setTeamFail(teamLose);
        match.setMatchStatus(MatchStatusEnum.CONFIRMED);
        MatchResponse response = convertMatchResponse(matchRepository.save(match));
        simpMessagingTemplate.convertAndSend(
                "/subscribe/confirm-match-active-status/" + room.getCode(),
                new ResponseGlobal<>(response)
        );

        List<UserTeam> leaders = userTeamRepository.findByTeamIdInAndRole(
                List.of(request.getTeamWinId(), teamLose.getId()), RoleEnum.ROLE_TEAM_LEADER);

        if (leaders.size() < 2) {
            throw new NotFoundExceptionHandler("Không tìm thấy một hoặc cả hai trưởng nhóm");
        }

        User teamWinLeader = leaders.get(0).getTeam().getId().equals(request.getTeamWinId()) ? leaders.get(0).getUser() : leaders.get(1).getUser();
        User teamLoseLeader = leaders.get(0).getTeam().getId().equals(teamLose.getId()) ? leaders.get(0).getUser() : leaders.get(1).getUser();

        int pointChange = room.getPointBetLevel();
        teamLoseLeader.setPoint(teamLoseLeader.getPoint() - pointChange);
        teamWinLeader.setPoint(teamWinLeader.getPoint() + pointChange);

        userRepository.saveAll(List.of(teamLoseLeader, teamWinLeader));

        messageService.sendMessage("/subscribe/fetch-transaction-active/" + teamWinLeader.getId(),
                modelMapper.map(teamWinLeader, UserResponse.class));

        messageService.sendMessage("/subscribe/fetch-transaction-active/" + teamLoseLeader.getId(),
                modelMapper.map(teamLoseLeader, UserResponse.class));

        return convertMatchResponse(match);
    }

    @Override
    public List<ResultMatchResponse> resultMatch(String teamOneId, String teamTwoId) {
        List<String> teamIds = Arrays.asList(teamOneId, teamTwoId);
        return matchRepository.countMatchByTeamIdAndMatchStatus(teamIds, MatchStatusEnum.CONFIRMED.name());
    }


    private MatchResponse convertMatchResponse(Match match) {
        return modelMapper.map(match, MatchResponse.class);
    }
}

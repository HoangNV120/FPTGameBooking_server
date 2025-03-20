package com.server.service.impl;

import com.server.dto.request.tournamentmatch.*;
import com.server.dto.response.tournamentmatch.TournamentMatchResponse;
import com.server.entity.*;
import com.server.enums.MatchStageEnum;
import com.server.enums.MatchTypeEnum;
import com.server.enums.ParticipationStatusEnum;
import com.server.enums.TournamentMatchStatusEnum;
import com.server.exceptions.RestApiException;
import com.server.repository.TeamTournamentParticipationRepository;
import com.server.repository.TournamentMatchRepository;
import com.server.repository.TournamentRepository;
import com.server.repository.UserRepository;
import com.server.service.TournamentMatchService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentMatchServiceImpl implements TournamentMatchService {

    private final TournamentRepository tournamentRepository;
    private final TournamentMatchRepository matchRepository;
    private final TeamTournamentParticipationRepository teamTournamentParticipationRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;


    /**
     * Đăng ký danh sách trận đấu cho một giải đấu
     * @param request Yêu cầu chứa danh sách trận đấu cần đăng ký
     * @return Danh sách trận đấu đã được đăng ký
     */
    @Override
    @Transactional
    public List<TournamentMatchResponse> submitTournamentMatches(ListSubmitTournamentMatch request) {
        // Tìm và xác thực giải đấu
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new RestApiException("Tournament not found"));

        if (!tournament.getUserCreate().equals(request.getUserId()) && !request.getUserId().equals("system")) {
            throw new RestApiException("Unauthorized");
        }

        // Kiểm tra tính hợp lệ của số lượng trận đấu
        int numberOfTeams = tournament.getNumberOfTeam();
        if (request.getMatchRequests().size() != numberOfTeams) {
            throw new RestApiException("Invalid number of matches for the given number of teams");
        }

        // Kiểm tra số đội tham gia với số đội đã đăng ký
        List<TeamTournamentParticipation> teamParticipations = teamTournamentParticipationRepository.findAllByTournamentId(tournament.getId());
        if (teamParticipations.size() != numberOfTeams) {
            throw new RestApiException("Number of teams in the tournament does not match the expected number");
        }

        // Gán các đội tham gia vào các trận đấu tương ứng
        List<TournamentMatch> matches = matchRepository.findAllByTournamentId(tournament.getId());
        for (SubmitTournamentMatchRequest matchRequest : request.getMatchRequests()) {
            int matchIndex = (matchRequest.getOrder() - 1) / 2;
            TournamentMatch match = matches.get(matchIndex);

            TeamTournamentParticipation team = teamParticipations.stream()
                    .filter(t -> t.getId().equals(matchRequest.getTeamId()))
                    .findFirst()
                    .orElseThrow(() -> new RestApiException("Team not found for ID: " + matchRequest.getTeamId()));

            // Xác định vị trí đội (team1 hoặc team2) dựa vào số thứ tự
            if ((matchRequest.getOrder() - 1) % 2 == 0) {
                match.setTeam1(team);
            } else {
                match.setTeam2(team);
            }
        }

        // Cập nhật trạng thái giải đấu và lưu thông tin
        tournament.setStatus("ONGOING");
        tournamentRepository.save(tournament);
        matchRepository.saveAll(matches);

        // Chuyển đổi và trả về danh sách trận đấu
        return matches.stream()
                .map(match -> modelMapper.map(match, TournamentMatchResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật điểm số cho một trận đấu
     * @param request Yêu cầu chứa thông tin điểm số mới
     * @return Thông tin trận đấu đã cập nhật
     */
    @Override
    @Transactional
    public TournamentMatchResponse updateMatchScore(UpdateScoreTournamentMatch request) {
        // Tìm trận đấu theo ID
        TournamentMatch match = matchRepository.findById(request.getTournamentMatchId())
                .orElseThrow(() -> new RestApiException("Match not found"));

        // Kiểm tra điểm số không được âm
        if (request.getTeam1Score() < 0 || request.getTeam2Score() < 0) {
            throw new RestApiException("Scores cannot be negative");
        }

        // Kiểm tra điểm số không vượt quá giới hạn cho phép
        int maxScore = getMaxScore(match.getType());
        if (request.getTeam1Score() > maxScore || request.getTeam2Score() > maxScore) {
            throw new RestApiException("Scores cannot exceed the maximum allowed for the match type");
        }

        // Kiểm tra tổng điểm không vượt quá format trận đấu (BO1/BO3/BO5)
        if (request.getTeam1Score() + request.getTeam2Score() > match.getType().getValue()) {
            throw new RestApiException("Total score cannot exceed the match format (BO1/BO3/BO5)");
        }

        // Cập nhật điểm số cho trận đấu
        match.setTeam1Score(request.getTeam1Score());
        match.setTeam2Score(request.getTeam2Score());

        // Cập nhật trạng thái trận đấu nếu đã đạt điểm thắng
        if (match.getTeam1Score() >= maxScore || match.getTeam2Score() >= maxScore) {
            match.setStatus(TournamentMatchStatusEnum.ENDED);

            // Cập nhật trận đấu tiếp theo nếu không phải trận chung kết
            if (match.getStage() != MatchStageEnum.FINALS) {
                if (match.getStage() == MatchStageEnum.THIRD_PLACE) {
                    updateThirdPlaceMatch(match);
                } else {
                    updateNextMatch(match);
                }
            } else {
                // Kết thúc giải đấu nếu là trận chung kết
                endTournament(match.getTournament());
            }
        }

        // Lưu trận đấu đã cập nhật vào cơ sở dữ liệu
        matchRepository.save(match);

        // Chuyển đổi và trả về thông tin trận đấu
        return modelMapper.map(match, TournamentMatchResponse.class);
    }

    /**
     * Lấy danh sách tất cả các trận đấu của một giải đấu
     * @param request Yêu cầu chứa ID của giải đấu
     * @return Danh sách trận đấu thuộc giải đấu
     */
    @Override
    public List<TournamentMatchResponse> getTournamentMatches(FindTournamentMatch request) {
        List<TournamentMatch> matches = matchRepository.findAllByTournamentId(request.getTournamentId());

        return matches.stream()
                .sorted(Comparator.comparing(TournamentMatch::getMatchOrder))
                .map(match -> modelMapper.map(match, TournamentMatchResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * Tính điểm tối đa cho mỗi loại trận đấu
     * @param type Loại trận đấu (BO1, BO3, BO5)
     * @return Điểm tối đa cho loại trận đấu
     */
    private int getMaxScore(MatchTypeEnum type) {
        return switch (type) {
            case BO1 -> 1;
            case BO3 -> 2;
            case BO5 -> 3;
            default -> throw new IllegalArgumentException("Unknown match type: " + type);
        };
    }

    /**
     * Kiểm tra và cập nhật trạng thái của các trận đấu theo thời gian
     * Được lên lịch chạy mỗi 5 phút
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    @Override
    public void checkAndUpdateMatchStatus() {
        // Chỉ lấy các trận đấu cần kiểm tra (WAITING hoặc ONGOING) để tối ưu hiệu suất
        List<TournamentMatch> waitingMatches = matchRepository.findAllByStatus(TournamentMatchStatusEnum.WAITING);
        List<TournamentMatch> ongoingMatches = matchRepository.findAllByStatus(TournamentMatchStatusEnum.ONGOING);
        LocalDateTime now = LocalDateTime.now();

        // Cập nhật trạng thái trận đấu từ WAITING sang ONGOING
        for (TournamentMatch match : waitingMatches) {
            if (match.getStartDate() != null && (match.getStartDate().isBefore(now) || match.getStartDate().equals(now))) {
                match.setStatus(TournamentMatchStatusEnum.ONGOING);
                matchRepository.save(match);
            }
        }

        // Cập nhật trạng thái trận đấu từ ONGOING sang ENDED
        for (TournamentMatch match : ongoingMatches) {
            if (match.getEndDate() != null && (match.getEndDate().isBefore(now) || match.getEndDate().equals(now))) {
                match.setStatus(TournamentMatchStatusEnum.ENDED);
                matchRepository.save(match);

                // Nếu trận đấu kết thúc mà chưa có điểm, tạo điểm ngẫu nhiên
                if (match.getTeam1Score() == null || match.getTeam2Score() == null) {
                    generateRandomScores(match);
                }

                // Cập nhật kết quả dựa vào loại trận đấu
                if (match.getStage() == MatchStageEnum.THIRD_PLACE) {
                    updateThirdPlaceMatch(match);
                } else if (match.getStage() != MatchStageEnum.FINALS) {
                    updateNextMatch(match);
                } else {
                    endTournament(match.getTournament());
                }
            }
        }
    }

    /**
     * Tạo điểm số ngẫu nhiên cho trận đấu khi kết thúc tự động
     * @param match Trận đấu cần tạo điểm
     */
    private void generateRandomScores(TournamentMatch match) {
        int maxScore = getMaxScore(match.getType());
        Random random = new Random();

        // Đảm bảo một đội đạt được điểm thắng tối đa
        int winningTeam = random.nextInt(2); // 0 hoặc 1

        if (winningTeam == 0) {
            // Team 1 thắng
            match.setTeam1Score(maxScore);
            match.setTeam2Score(random.nextInt(maxScore));
        } else {
            // Team 2 thắng
            match.setTeam2Score(maxScore);
            match.setTeam1Score(random.nextInt(maxScore));
        }

        // Đảm bảo tổng điểm không vượt quá giới hạn
        while (match.getTeam1Score() + match.getTeam2Score() > match.getType().getValue()) {
            if (winningTeam == 0) {
                match.setTeam2Score(random.nextInt(maxScore));
            } else {
                match.setTeam1Score(random.nextInt(maxScore));
            }
        }
    }

    /**
     * Cập nhật kết quả cho trận tranh hạng 3 sau khi kết thúc
     * Xử lý trường hợp hòa bằng cách ngẫu nhiên cộng điểm cho một đội
     * Phân định hạng 3 và hạng 4 cho hai đội dựa trên điểm số
     * @param match Trận đấu tranh hạng 3 cần cập nhật
     */
    private void updateThirdPlaceMatch(TournamentMatch match) {
        if (Objects.equals(match.getTeam1Score(), match.getTeam2Score())) {
            generateRandomScores(match);
        }

        // Xác định đội hạng 3 và hạng 4 dựa trên điểm số
        if (match.getTeam1Score() > match.getTeam2Score()) {
            match.getTeam2().setPlace(4);
            match.getTeam1().setPlace(3);
        } else {
            match.getTeam1().setPlace(4);
            match.getTeam2().setPlace(3);
        }

        // Đặt trạng thái kết thúc cho cả hai đội
        match.getTeam1().setStatus(ParticipationStatusEnum.ENDED);
        match.getTeam2().setStatus(ParticipationStatusEnum.ENDED);

        // Lưu thay đổi vào cơ sở dữ liệu
        matchRepository.save(match);
        teamTournamentParticipationRepository.save(match.getTeam1());
        teamTournamentParticipationRepository.save(match.getTeam2());
    }

    /**
     * Cập nhật trận đấu tiếp theo sau khi trận hiện tại kết thúc
     * Xác định đội thắng/thua, cập nhật hạng của đội thua và đưa đội thắng vào trận tiếp theo
     * Xử lý trường hợp đặc biệt cho bán kết khi có trận tranh hạng 3
     * @param match Trận đấu hiện tại đã kết thúc
     */
    private void updateNextMatch(TournamentMatch match) {
        // Lấy thông tin giải đấu và danh sách trận đấu
        Tournament tournament = match.getTournament();
        List<TournamentMatch> matches = matchRepository.findAllByTournamentId(tournament.getId());

        // Xác định trận đấu tiếp theo dựa trên thứ tự trận hiện tại
        int nextMatchOrder = getNextMatchOrder(match.getMatchOrder(), matches.size(), tournament.isThirdPlaceMatch());
        TournamentMatch nextMatch = matches.stream()
                .filter(m -> m.getMatchOrder() == nextMatchOrder)
                .findFirst()
                .orElseThrow(() -> new RestApiException("Next match not found"));

        // Xử lý trường hợp hòa bằng cách ngẫu nhiên cộng điểm cho một đội
        if (Objects.equals(match.getTeam1Score(), match.getTeam2Score())) {
            if (Math.random() < 0.5) {
                match.setTeam1Score(match.getTeam1Score() + 1);
            } else {
                match.setTeam2Score(match.getTeam2Score() + 1);
            }
        }

        // Xác định đội thắng và đội thua dựa trên điểm số
        TeamTournamentParticipation losingTeam;
        TeamTournamentParticipation winningTeam;

        if (match.getTeam1Score() > match.getTeam2Score()) {
            winningTeam = match.getTeam1();
            losingTeam = match.getTeam2();
        } else {
            winningTeam = match.getTeam2();
            losingTeam = match.getTeam1();
        }

        // Đặt hạng cho đội thua dựa trên vòng đấu
        if (match.getStage() == MatchStageEnum.ROUND_OF_16) {
            losingTeam.setPlace(16);
        } else if (match.getStage() == MatchStageEnum.QUARTER_FINALS) {
            losingTeam.setPlace(8);
        } else if (match.getStage() == MatchStageEnum.SEMI_FINALS) {
            losingTeam.setPlace(4);
        }

        // Đặt trạng thái kết thúc cho đội thua
        losingTeam.setStatus(ParticipationStatusEnum.ENDED);

        // Xử lý đội thua bán kết cho trận tranh hạng 3 (nếu có)
        if (match.getStage() == MatchStageEnum.SEMI_FINALS && tournament.isThirdPlaceMatch()) {
            int thirdPlaceMatchOrder = matches.size() - 1;
            TournamentMatch thirdPlaceMatch = matches.stream()
                    .filter(m -> m.getMatchOrder() == thirdPlaceMatchOrder)
                    .findFirst()
                    .orElse(null);

            if (thirdPlaceMatch != null) {
                // Đặt đội thua vào vị trí phù hợp trong trận tranh hạng 3
                if (match.getMatchOrder() % 2 == 1) { // Số trận lẻ
                    thirdPlaceMatch.setTeam1(losingTeam);
                } else { // Số trận chẵn
                    thirdPlaceMatch.setTeam2(losingTeam);
                }
                matchRepository.save(thirdPlaceMatch);
            }
        }

        // Cập nhật đội thắng vào trận đấu tiếp theo
        // Đội thắng của trận có số thứ tự lẻ sẽ là team1 của trận tiếp theo
        // Đội thắng của trận có số thứ tự chẵn sẽ là team2 của trận tiếp theo
        if (match.getMatchOrder() % 2 == 1) { // Số trận lẻ
            nextMatch.setTeam1(winningTeam);
        } else { // Số trận chẵn
            nextMatch.setTeam2(winningTeam);
        }

        // Lưu tất cả thay đổi vào cơ sở dữ liệu
        matchRepository.save(nextMatch);
        matchRepository.save(match);
        teamTournamentParticipationRepository.save(winningTeam);
        teamTournamentParticipationRepository.save(losingTeam);
    }



    /**
     * Xác định thứ tự của trận đấu tiếp theo dựa trên thứ tự trận hiện tại
     * @param currentOrder Thứ tự trận đấu hiện tại
     * @param totalMatches Tổng số trận đấu của giải
     * @param hasThirdPlaceMatch Có trận tranh hạng 3 hay không
     * @return Thứ tự của trận đấu tiếp theo
     */
    private int getNextMatchOrder(int currentOrder, int totalMatches, boolean hasThirdPlaceMatch) {
        // Tính tổng số trận đấu không bao gồm trận tranh hạng 3 (nếu có)
        int actualTotalMatches = hasThirdPlaceMatch ? totalMatches - 1 : totalMatches;

        // Xử lý cho giải đấu 16 đội (15 trận)
        if (actualTotalMatches == 15) {
            // Vòng 1/8 (trận thứ 1-8)
            if (currentOrder <= 8) {
                // Trận 1 và 2 đi tới trận 9
                // Trận 3 và 4 đi tới trận 10
                // Trận 5 và 6 đi tới trận 11
                // Trận 7 và 8 đi tới trận 12
                return 8 + (int)Math.ceil(currentOrder / 2.0);
            }
            // Tứ kết (trận thứ 9-12)
            else if (currentOrder <= 12) {
                // Trận 9 và 10 đi tới trận 13
                // Trận 11 và 12 đi tới trận 14
                return 12 + (int)Math.ceil((currentOrder - 8) / 2.0);
            }
            // Bán kết (trận thứ 13-14)
            else if (currentOrder <= 14) {
                // Cả hai trận đều đi tới chung kết (trận 15)
                return totalMatches;
            }
        }
        // Xử lý cho giải đấu 8 đội (7 trận)
        else if (actualTotalMatches == 7) {
            // Tứ kết (trận thứ 1-4)
            if (currentOrder <= 4) {
                // Trận 1 và 2 đi tới trận 5
                // Trận 3 và 4 đi tới trận 6
                return 4 + (int)Math.ceil(currentOrder / 2.0);
            }
            // Bán kết (trận thứ 5-6)
            else if (currentOrder <= 6) {
                // Cả hai trận đều đi tới chung kết (trận 7)
                return totalMatches;
            }
        }
        // Xử lý cho giải đấu 4 đội (3 trận)
        else if (actualTotalMatches == 3) {
            // Bán kết (trận thứ 1-2)
            if (currentOrder <= 2) {
                // Cả hai trận đều đi tới chung kết (trận 3)
                return totalMatches;
            }
        }

        throw new IllegalArgumentException("Invalid match order: " + currentOrder + " for total matches: " + totalMatches);
    }

    /**
     * Kết thúc giải đấu và cập nhật trạng thái cho tất cả đội tham gia
     * @param tournament Giải đấu cần kết thúc
     */
    private void endTournament(Tournament tournament) {
        // Đặt trạng thái giải đấu thành "KẾT THÚC"
        tournament.setStatus("ENDED");
        tournamentRepository.save(tournament);

        // Cập nhật trạng thái tất cả đội tham gia thành "KẾT THÚC"
        List<TeamTournamentParticipation> participations = teamTournamentParticipationRepository.findAllByTournamentId(tournament.getId());
        for (TeamTournamentParticipation participation : participations) {
            participation.setStatus(ParticipationStatusEnum.ENDED);
            teamTournamentParticipationRepository.save(participation);
        }

        // Phân phối giải thưởng cho các đội đứng đầu
        distributeTopPrizes(tournament, participations);
    }

    /**
     * Phân phối giải thưởng cho các đội đứng đầu (hạng 1 và hạng 2)
     * @param tournament Giải đấu cần phân phối giải thưởng
     * @param participations Danh sách đội tham gia
     */
    private void distributeTopPrizes(Tournament tournament, List<TeamTournamentParticipation> participations) {
        // Tìm trận chung kết
        TournamentMatch finalMatch = matchRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(match -> match.getStage() == MatchStageEnum.FINALS)
                .findFirst()
                .orElseThrow(() -> new RestApiException("Final match not found"));

        TeamTournamentParticipation winningTeam;
        TeamTournamentParticipation losingTeam;

        // Xử lý trường hợp hòa trong trận chung kết
        if (Objects.equals(finalMatch.getTeam1Score(), finalMatch.getTeam2Score())) {
            // Thêm 1 điểm ngẫu nhiên cho một đội để xác định đội thắng
            if (Math.random() < 0.5) {
                finalMatch.setTeam1Score(finalMatch.getTeam1Score() + 1);
            } else {
                finalMatch.setTeam2Score(finalMatch.getTeam2Score() + 1);
            }
            matchRepository.save(finalMatch);
        }

        // Xác định đội vô địch và đội á quân
        if (finalMatch.getTeam1Score() > finalMatch.getTeam2Score()) {
            winningTeam = finalMatch.getTeam1();
            losingTeam = finalMatch.getTeam2();
        } else {
            winningTeam = finalMatch.getTeam2();
            losingTeam = finalMatch.getTeam1();
        }

        // Đặt hạng và trạng thái cho đội vô địch và á quân
        winningTeam.setPlace(1);
        losingTeam.setPlace(2);
        winningTeam.setStatus(ParticipationStatusEnum.ENDED);
        losingTeam.setStatus(ParticipationStatusEnum.ENDED);

        // Lưu thông tin đội tham gia vào CSDL
        teamTournamentParticipationRepository.save(winningTeam);
        teamTournamentParticipationRepository.save(losingTeam);

        // Cập nhật điểm thưởng nếu giải đấu có giải thưởng
        if(tournament.getTop1Prize()!=null && tournament.getTop2Prize()!=null && tournament.getTop1Prize()>0 && tournament.getTop2Prize()>0){
            int top1Prize = tournament.getTop1Prize();
            int top2Prize = tournament.getTop2Prize();

            updateTeamPoints(winningTeam, top1Prize);
            updateTeamPoints(losingTeam, top2Prize);
        }
    }

    /**
     * Cập nhật điểm cho tất cả thành viên trong đội
     * Chia đều giải thưởng cho các thành viên trong đội
     * @param team Đội cần cập nhật điểm
     * @param prize Giải thưởng tổng của đội
     */
    private void updateTeamPoints(TeamTournamentParticipation team, int prize) {
        // Lấy danh sách thành viên trong đội
        List<UserTeamTournament> userTeamTournaments = team.getTeam().getUserTeamTournaments();
        // Tính toán giải thưởng cho mỗi thành viên (làm tròn lên)
        int prizePerUser = (int) Math.ceil((double) prize / userTeamTournaments.size());

        // Cập nhật điểm cho từng thành viên
        for (UserTeamTournament userTeamTournament : userTeamTournaments) {
            User user = userTeamTournament.getUser();
            user.setPoint(user.getPoint() + prizePerUser);
            userRepository.save(user);
        }
    }
}
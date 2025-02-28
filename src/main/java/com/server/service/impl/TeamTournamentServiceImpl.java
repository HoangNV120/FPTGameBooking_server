package com.server.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.server.constants.Constants;
import com.server.dto.request.teamtournament.CreateTeamTournament;
import com.server.dto.request.teamtournament.FindTeamTournament;
import com.server.dto.request.teamtournament.UpdateTeamTournament;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.teamtournament.TeamTournamentImageResponse;
import com.server.dto.response.teamtournament.TeamTournamentResponse;
import com.server.entity.TeamTournament;
import com.server.entity.User;
import com.server.entity.UserTeamTournament;
import com.server.enums.TeamTournamentRoleEnum;
import com.server.exceptions.RestApiException;
import com.server.repository.TeamTournamentRepository;
import com.server.repository.UserRepository;
import com.server.repository.UserTeamTournamentRepository;
import com.server.repository.specifications.TeamTournamentSpecification;
import com.server.service.TeamTournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamTournamentServiceImpl implements TeamTournamentService {

    private final TeamTournamentRepository teamTournamentRepository;
    private final UserRepository userRepository;
    private final UserTeamTournamentRepository userTeamTournamentRepository;
    private final ModelMapper modelMapper;
    private final Cloudinary cloudinary;

    @Override
    public PageableObject<TeamTournamentResponse> findAll(FindTeamTournament request) {
        Specification<TeamTournament> spec = Specification.where(TeamTournamentSpecification.isNotDeleted());

        if (request.getName() != null) {
            spec = spec.and(TeamTournamentSpecification.hasNameLike(request.getName()));
        }

        if (request.getCountUserTeamOrder() != null) {
            spec = spec.and(TeamTournamentSpecification.orderByMemberCount(request.getCountUserTeamOrder()));
        }

        PageRequest pageRequest = PageRequest.of(request.getPageNo(), request.getPageSize());
        Page<TeamTournament> page = teamTournamentRepository.findAll(spec, pageRequest);

        return new PageableObject<>(page.map(teamTournament -> {
            TeamTournamentResponse response = convertToResponse(teamTournament);
            int recentMemberCount = userTeamTournamentRepository.countByTeamId(teamTournament.getId());
            response.setRecentMemberCount(recentMemberCount);
            return response;
        }));
    }

    @Override
    public TeamTournamentResponse add(CreateTeamTournament dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        //check if user already has a team
        if (userTeamTournamentRepository.existsByUserId(user.getId())) {
            throw new RestApiException("User already has a team");
        }

        TeamTournament teamTournament = convertToEntity(dto);
        teamTournament.setImageLink(Constants.DEFAULT_URL_LOGO_TEAM);
        teamTournament = teamTournamentRepository.save(teamTournament);

        UserTeamTournament userTeamTournament = new UserTeamTournament();
        userTeamTournament.setUser(user);
        userTeamTournament.setTeam(teamTournament);
        userTeamTournament.setTeamRole(TeamTournamentRoleEnum.LEADER);
        userTeamTournamentRepository.save(userTeamTournament);

        return convertToResponse(teamTournament);
    }

    @Override
    public TeamTournamentResponse update(UpdateTeamTournament dto) {
        TeamTournament teamTournament = teamTournamentRepository.findById(dto.getId())
                .orElseThrow(() -> new RestApiException("Team not found"));

        teamTournament.setName(dto.getName());
        teamTournament.setDescription(dto.getDescription());
        teamTournament.setImageLink(dto.getImageLink());

        teamTournament = teamTournamentRepository.save(teamTournament);
        return convertToResponse(teamTournament);

    }

    @Override
    public TeamTournamentResponse getById(String id) {
        TeamTournament teamTournament = teamTournamentRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new RestApiException("Team not found or has been deleted"));

        TeamTournamentResponse response = convertToResponse(teamTournament);
        int recentMemberCount = userTeamTournamentRepository.countByTeamId(teamTournament.getId());
        response.setRecentMemberCount(recentMemberCount);

        return response;
    }

    @Override
    public TeamTournamentImageResponse uploadImage(MultipartFile file, String teamId) throws IOException {
        // Delete old image if exists
        deleteImage("team" + teamId);

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("public_id", "team" + teamId, "resource_type", "auto"));

            // Get team tournament by ID
            TeamTournament teamTournament = teamTournamentRepository.findById(teamId)
                    .orElseThrow(() -> new RestApiException("Team not found"));

            // Update team tournament image link
            teamTournament.setImageLink(uploadResult.get("secure_url").toString());
            teamTournamentRepository.save(teamTournament);

            return new TeamTournamentImageResponse(uploadResult.get("secure_url").toString());

    }


    private void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.out.println("Cannot delete old image: " + e.getMessage());
        }
    }

    private TeamTournamentResponse convertToResponse(TeamTournament teamTournament) {
        return modelMapper.map(teamTournament, TeamTournamentResponse.class);
    }

    private TeamTournament convertToEntity(CreateTeamTournament createTeamTournament) {
        return modelMapper.map(createTeamTournament, TeamTournament.class);
    }


}

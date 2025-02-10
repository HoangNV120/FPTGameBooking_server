package com.server.service.impl;

import com.server.dto.request.team.CreateTeamRequest;
import com.server.dto.request.team.FindTeamRequest;
import com.server.dto.request.team.UpdateTeamRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.team.TeamResponse;
import com.server.entity.Team;
import com.server.exceptions.NotFoundExceptionHandler;
import com.server.repository.TeamRepository;
import com.server.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    /**
     * Retrieves all teams with pagination support.
     *
     * @param request The request containing pagination information such as page number and size.
     * @return A PageableObject containing a list of TeamResponse objects and pagination metadata.
     */
    @Override
    public PageableObject<TeamResponse> findAll(FindTeamRequest request) {
        log.info("Find all teams with request: {}", request);

        return null;
    }

    /**
     * Adds a new team based on the provided CreateTeamRequest.
     *
     * @param dto The DTO containing the information for creating a new team.
     * @return The TeamResponse object representing the newly created team.
     */
    @Override
    public TeamResponse add(CreateTeamRequest dto) {
        log.info("Create team request: {}", dto);

        Team team = modelMapper.map(dto, Team.class);
        teamRepository.save(team);

        return convertTeamResponse(team);
    }

    /**
     * Updates an existing team based on the provided UpdateTeamRequest.
     *
     * @param dto The DTO containing the updated information for an existing team.
     * @return The TeamResponse object representing the updated team.
     * @throws NotFoundExceptionHandler if the team to update is not found.
     */
    @Override
    public TeamResponse update(UpdateTeamRequest dto) {
        log.info("Update team request: {}", dto);

        Team team = teamRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundExceptionHandler("Không tìm thấy đội"));

        modelMapper.map(dto, team);
        teamRepository.save(team);

        return convertTeamResponse(team);
    }

    /**
     * Retrieves a team by its ID.
     *
     * @param id The ID of the team to retrieve.
     * @return The TeamResponse object representing the team with the given ID.
     * @throws NotFoundExceptionHandler if the team with the provided ID is not found.
     */
    @Override
    public TeamResponse getById(String id) {
        log.info("Get team by id: {}", id);

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundExceptionHandler("Không tìm thấy đội"));

        return convertTeamResponse(team);
    }

    /**
     * Converts a Team entity to a TeamResponse DTO.
     *
     * @param team The Team entity to convert.
     * @return The corresponding TeamResponse DTO.
     */
    private TeamResponse convertTeamResponse(Team team) {
        return modelMapper.map(team, TeamResponse.class);
    }

    /**
     * Finds a team by its name.
     *
     * @param name The name of the team to find.
     * @return The TeamResponse object representing the found team.
     * @throws NotFoundExceptionHandler if no team with the provided name is found.
     */
    @Override
    public TeamResponse findTeamByName(String name) {
        log.info("findTeamByName==> name = {}", name);

        Team team = teamRepository.findByName(name)
                .orElseThrow(() -> new NotFoundExceptionHandler("Không tìm thấy đội"));

        return convertTeamResponse(team);
    }

    /**
     * findAllTeamsByCodeRoom a team by its name.
     *
     * @param codeRoom The codeRoom of the room to find.
     * @return The TeamResponse object representing the found team.
     * @throws NotFoundExceptionHandler if no team with the provided name is found.
     */
    @Override
    public List<TeamResponse> findAllTeamsByCodeRoom(String codeRoom) {
        log.info("findAllTeamsByCodeRoom==> codeRoom = {}", codeRoom);
        return teamRepository.findByRoom_code(codeRoom)
                .stream()
                .map(this::convertTeamResponse)
                .toList();
    }

}

package com.server.repository.specifications;

import com.server.entity.TeamTournament;
import com.server.entity.UserTeamTournament;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class TeamTournamentSpecification {

    public static Specification<TeamTournament> hasNameLike(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<TeamTournament> orderByMemberCount(String order) {
        return (root, query, criteriaBuilder) -> {
            Join<TeamTournament, UserTeamTournament> join = root.join("userTeamTournaments", JoinType.LEFT);
            query.groupBy(root.get("id"));
            if ("asc".equalsIgnoreCase(order)) {
                query.orderBy(criteriaBuilder.asc(criteriaBuilder.count(join.get("id"))));
            } else {
                query.orderBy(criteriaBuilder.desc(criteriaBuilder.count(join.get("id"))));
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<TeamTournament> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isDeleted"), false);
    }
}
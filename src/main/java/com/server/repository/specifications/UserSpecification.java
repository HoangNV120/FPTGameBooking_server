package com.server.repository.specifications;

import com.server.dto.request.user.FindUserRequest;
import com.server.entity.User;
import com.server.enums.LevelEnum;
import com.server.enums.StatusEnum;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasId(String id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), id);
    }

    public static Specification<User> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<User> hasLevel(LevelEnum level) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("level"), level);
    }

    public static Specification<User> hasStatus(StatusEnum status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<User> orderByPoint(boolean ascending) {
        return (root, query, criteriaBuilder) -> {
            if (ascending) {
                query.orderBy(criteriaBuilder.asc(root.get("point")));
            } else {
                query.orderBy(criteriaBuilder.desc(root.get("point")));
            }
            return null;
        };
    }

    public static Specification<User> orderByCreatedDate(boolean ascending) {
        return (root, query, criteriaBuilder) -> {
            if (ascending) {
                query.orderBy(criteriaBuilder.asc(root.get("createdDate")));
            } else {
                query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            }
            return null;
        };
    }

    public static Specification<User> orderByUpdatedDate(boolean ascending) {
        return (root, query, criteriaBuilder) -> {
            if (ascending) {
                query.orderBy(criteriaBuilder.asc(root.get("updatedDate")));
            } else {
                query.orderBy(criteriaBuilder.desc(root.get("updatedDate")));
            }
            return null;
        };
    }

    public static Specification<User> getSpecifications(FindUserRequest request) {
        Specification<User> spec = Specification.where(null);

        if (request.getId() != null) {
            spec = spec.and(hasId(request.getId()));
        }
        if (request.getName() != null) {
            spec = spec.and(hasName(request.getName()));
        }
        if (request.getLevel() != null) {
            spec = spec.and(hasLevel(LevelEnum.fromString(request.getLevel())));
        }
        if (request.getStatus() != null) {
            spec = spec.and(hasStatus(StatusEnum.fromString(request.getStatus())));
        }
        if (request.getPointOrder() != null) {
            spec = spec.and(orderByPoint("asc".equalsIgnoreCase(request.getPointOrder())));
        }
        if (request.getCreatedDateOrder() != null) {
            spec = spec.and(orderByCreatedDate("asc".equalsIgnoreCase(request.getCreatedDateOrder())));
        }
        if (request.getUpdatedDateOrder() != null) {
            spec = spec.and(orderByUpdatedDate("asc".equalsIgnoreCase(request.getUpdatedDateOrder())));
        }

        return spec;
    }
}
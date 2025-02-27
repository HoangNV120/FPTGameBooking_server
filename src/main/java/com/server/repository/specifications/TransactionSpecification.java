package com.server.repository.specifications;

import com.server.dto.request.transaction.FindTransactionRequest;
import com.server.entity.Transaction;
import com.server.enums.TransactionEnum;
import com.server.exceptions.NotFoundExceptionHandler;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> buildSpecification(FindTransactionRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm kiếm theo ID của giao dịch
            if (request.getTransactionId() != null && !request.getTransactionId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("id"), request.getTransactionId()));
            }

            // Tìm kiếm theo ID của userApprove (chính là User duoc phê duyệt giao dịch)
            if (request.getUserId() != null && !request.getUserId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("userApprove").get("id"), request.getUserId()));
            }

            // Tìm kiếm theo số tiền (amount)
            if (request.getAmountOrder() != null && !request.getAmountOrder().isEmpty()) {
                if ("asc".equalsIgnoreCase(request.getAmountOrder())) {
                    query.orderBy(criteriaBuilder.asc(root.get("amount")));
                }
                if ("desc".equalsIgnoreCase(request.getAmountOrder())) {
                    query.orderBy(criteriaBuilder.desc(root.get("amount")));
                }
            }

            // Tìm kiếm theo trạng thái giao dịch
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                try {
                    TransactionEnum transactionEnum = TransactionEnum.fromString(request.getStatus());
                    predicates.add(criteriaBuilder.equal(root.get("transactionStatus"), transactionEnum));
                } catch (NotFoundExceptionHandler e) {
                    throw new IllegalArgumentException("Trạng thái giao dịch không hợp lệ: " + request.getStatus());
                }
            }

            // Sắp xếp theo ngày tạo (createdDate từ AuditTable)
            if (request.getCreatedDateOrder() != null && !request.getCreatedDateOrder().isEmpty()) {
                if ("asc".equalsIgnoreCase(request.getCreatedDateOrder())) {
                    query.orderBy(criteriaBuilder.asc(root.get("createdDate")));
                }
                if ("desc".equalsIgnoreCase(request.getCreatedDateOrder())) {
                    query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
                }
            }

            // Sắp xếp theo ngày cập nhật (updatedDate từ AuditTable)
            if (request.getUpdatedDateOrder() != null && !request.getUpdatedDateOrder().isEmpty()) {
                if ("asc".equalsIgnoreCase(request.getUpdatedDateOrder())) {
                    query.orderBy(criteriaBuilder.asc(root.get("updatedDate")));
                }
                if ("desc".equalsIgnoreCase(request.getUpdatedDateOrder())) {
                    query.orderBy(criteriaBuilder.desc(root.get("updatedDate")));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}


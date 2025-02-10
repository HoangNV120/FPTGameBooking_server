package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.TransactionEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Transaction extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String bank;
    private String consumerId;
    private BigDecimal amount;
    private int point;

    @ManyToOne
    @JoinColumn(name = "id_user_approve")
    private User userApprove;

    @Enumerated(EnumType.STRING)
    private TransactionEnum transactionStatus;
}

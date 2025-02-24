package com.server.entity;

import com.server.entity.common.AuditTable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "team_tournament")
public class TeamTournament extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int memberCount;

    @Column(columnDefinition = "text")
    private String description;

    private String imageLink;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isDeleted;
}
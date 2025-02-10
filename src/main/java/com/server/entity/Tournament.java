package com.server.entity;


import com.server.entity.common.AuditTable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Tournament extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_game")
    private Game game;

    private String name;
    private int numberOfTeam;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String link;
    private boolean flagDisplay;
    private String status;
}

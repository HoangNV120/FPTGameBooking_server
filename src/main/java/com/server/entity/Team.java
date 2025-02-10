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
@Table
public class Team extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_room")
    private Room room;
    @OneToOne(mappedBy = "team", cascade = CascadeType.ALL)
    private UserTeam userTeam;
}

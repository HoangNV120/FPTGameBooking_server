package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.LevelRoomEnum;
import com.server.enums.PlayerModeEnum;
import com.server.enums.RankEnum;
import com.server.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Room extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_game")
    private Game game;

    private String name;
    private String code;
    private String nameUser;
    private Integer pointBetLevel;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = RankEnum.class)
    @CollectionTable(name = "room_rank", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "rank_player")
    @Enumerated(EnumType.STRING)
    private List<RankEnum> rankPlayer;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = PlayerModeEnum.class)
    @CollectionTable(name = "room_player_mode", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "player_mode")
    @Enumerated(EnumType.STRING)
    private List<PlayerModeEnum> playerModes;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    @Enumerated(EnumType.STRING)
    private LevelRoomEnum level;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();
}

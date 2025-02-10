package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.DisplayEnum;
import com.server.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Game extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String code;
    private String name;
    private String banner;
    @Enumerated(EnumType.STRING)
    private DisplayEnum display;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}

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
public class Image extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String idType;
    private String url;
    private String name;
}

package com.server.entity.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.server.util.LocalDateTimeToAsiaHoChiMinhSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditTable implements Serializable {

    @CreatedBy
    @Column(name = "user_create", updatable = false)
    private String userCreate;

    @LastModifiedBy
    @Column(name = "user_update")
    private String userUpdate;

    @CreatedDate
    @Column(name = "create_date", updatable = false)
    @JsonSerialize(using = LocalDateTimeToAsiaHoChiMinhSerializer.class) // Serialize thành GMT+7
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "update_date")
    @JsonSerialize(using = LocalDateTimeToAsiaHoChiMinhSerializer.class) // Serialize thành GMT+7
    private LocalDateTime updatedDate;
}

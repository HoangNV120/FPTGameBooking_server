package com.server.entity;

import com.server.entity.common.AuditTable;
import com.server.enums.LevelEnum;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class User extends AuditTable implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String email;
    private int point;
    @Enumerated(EnumType.STRING)
    private LevelEnum level;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    private String password;
    private String avatar;
    private String activeToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

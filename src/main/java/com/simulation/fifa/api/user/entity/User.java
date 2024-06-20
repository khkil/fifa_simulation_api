package com.simulation.fifa.api.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Long id;

    @Column(unique = true)
    private String accountId;

    @Enumerated(EnumType.STRING)
    private OAuthType oAuthType;
}

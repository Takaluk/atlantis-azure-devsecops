package com.stocklens.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(schema = "usr", name = "app_user")
public class AppUserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Setter
    @Column(name = "password_hash", length = 255)
    private String passwordHash;
}

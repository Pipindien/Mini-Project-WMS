package com.users.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "token")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tokenId;

    @Column(nullable = false, unique = true)
    private String jwtToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredDate;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Users user;
}

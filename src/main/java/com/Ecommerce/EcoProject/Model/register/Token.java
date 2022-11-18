package com.Ecommerce.EcoProject.Model.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.security.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token {
        @Id
        @GeneratedValue( strategy = GenerationType.AUTO)
        @Column(name = "Token_ID")
        Long id;

        @Column(name = "EMAIL")
        String email;

        @Column(name = "TOKEN",unique = true)
        private String token;

        @Column(name = "FORGOT_PASS_TOKEN",unique = true)
        private String forgotPassToken;

        @CreationTimestamp
        @Column(name = "TIME_STAMP",updatable = false)
        private Timestamp timeStamp;

        @Column(name = "EXPIRE_AT",updatable = false)
        @Basic(optional = false)
        private LocalDateTime expireAt;

        @OneToOne
        @JoinColumn(name = "USER_ID")
        private EntityUser user;

        @Transient
        private boolean isExpired;
    }

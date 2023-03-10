package org.ttn.ecommerce.entity.token;

import lombok.Data;
import org.ttn.ecommerce.controller.auditing.Auditable;
import org.ttn.ecommerce.entity.register.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class ActivateUserToken extends Auditable<String> {
    @Id
    @SequenceGenerator(name="activate_sequence",sequenceName = "activate_sequence",initialValue = 1,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "activate_sequence")
    private Long id;

    @Column(name="token")
    private String token;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="expire_At")
    private LocalDateTime expireAt;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity userEntity;
}

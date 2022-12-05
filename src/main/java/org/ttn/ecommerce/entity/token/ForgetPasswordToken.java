package org.ttn.ecommerce.entity.token;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ttn.ecommerce.controller.auditing.Auditable;
import org.ttn.ecommerce.entity.register.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class ForgetPasswordToken extends Auditable<String> {
    @Id
    @SequenceGenerator(name="forget_pass_sequence",sequenceName = "forget_pass_sequence",initialValue = 1,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "forget_pass_sequence")
    private Long id;

    private String token;

    private LocalDateTime createdAt;

    private LocalDateTime expireAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity userEntity;

    public ForgetPasswordToken(Long id, String token, LocalDateTime createdAt, LocalDateTime expireAt, UserEntity userEntity) {
        this.id = id;
        this.token = token;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
        this.userEntity = userEntity;
    }
}

package org.ttn.ecommerce.entity.token;


import lombok.Data;
import org.ttn.ecommerce.controller.auditing.Auditable;
import org.ttn.ecommerce.entity.register.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class RefreshToken extends Auditable<String> {
    @Id
    @SequenceGenerator(name="refresh_token_sequence",sequenceName = "refresh_token_sequence",initialValue = 1,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "refresh_token_sequence")
    private Long id;

    private String token;

    private LocalDateTime expireAt;

    @OneToOne
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private UserEntity userEntity;


}

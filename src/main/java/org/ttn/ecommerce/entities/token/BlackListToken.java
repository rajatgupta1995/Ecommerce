package org.ttn.ecommerce.entities.token;

import lombok.Data;
import org.ttn.ecommerce.entities.register.UserEntity;

import javax.persistence.*;

@Data
@Entity
public class BlackListToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
//    private String email;
    private String token;


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private UserEntity userEntity;
}

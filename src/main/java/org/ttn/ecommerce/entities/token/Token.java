package org.ttn.ecommerce.entities.token;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ttn.ecommerce.entities.register.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "access_token")
public class Token {
    @Id
    @SequenceGenerator(name="user_sequence",sequenceName = "user_sequence",initialValue = 1,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "user_sequence")
    private Long id;

    @Column(name="token")
    private String token;

    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private UserEntity userEntity;

}

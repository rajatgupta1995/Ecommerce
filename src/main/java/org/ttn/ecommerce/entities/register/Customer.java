package org.ttn.ecommerce.entities.register;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name="customer")
@PrimaryKeyJoinColumn(name = "user_id",referencedColumnName = "id")
public class Customer  extends UserEntity {

    @Column(name = "contact")
    @NotNull
    private String contact;
}

package org.ttn.ecommerce.entities.register;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@PrimaryKeyJoinColumn(name="user_id",referencedColumnName = "id")
@Table(name="seller")
@Data
public class Seller extends UserEntity {

    @NotNull
    private String gst;

    @NotNull
    private String companyContact;

    @NotNull
    private String companyName;
}

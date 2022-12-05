package org.ttn.ecommerce.entity.register;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
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

package org.ttn.ecommerce.entity.register;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@PrimaryKeyJoinColumn(name="user_id",referencedColumnName = "id")
@Table(name="seller")
@Data
@JsonFilter("sellerFilter")
public class Seller extends UserEntity {

    @NotNull
    private String gst;

    @NotNull
    private String companyContact;

    @NotNull
    private String companyName;
}

package com.Ecommerce.EcoProject.Model.register;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@PrimaryKeyJoinColumn(name = "user_id")
@JsonFilter("customerFilter")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends EntityUser {

    @Column(name = "CONTACT")
    @NotNull
    private String contact;
}

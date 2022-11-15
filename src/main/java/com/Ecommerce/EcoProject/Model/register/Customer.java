package com.Ecommerce.EcoProject.Model.register;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.sun.istack.NotNull;

import javax.persistence.*;


@Entity
@PrimaryKeyJoinColumn(name = "user_id")
@JsonFilter("customerFilter")
public class Customer extends EntityUser {

    @Column(name = "CONTACT")
    @NotNull
    private String contact;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}

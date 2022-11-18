package com.Ecommerce.EcoProject.Model.register;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@JsonFilter("SellerFilter")
public class Seller extends EntityUser {

    @Column(name = "GST")
    @NotNull
    private String gst;

    @Column(name = "COMPANY_CONTACT")
    @NotNull
    private String companyContact;

    @Column(name = "COMPANY_NAME")
    @NotNull
    private String companyName;
}

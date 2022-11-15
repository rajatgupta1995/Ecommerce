package com.Ecommerce.EcoProject.Model.register;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
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


    //getters and setters
    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getCompanyContact() {
        return companyContact;
    }

    public void setCompanyContact(String companyContact) {
        this.companyContact = companyContact;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}

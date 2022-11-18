package com.Ecommerce.EcoProject.Model.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String city;
    private String state;
    private String country;

    @Column(name = "ADDRESS_LINE")
    private String addressLine;

    @Column(name = "ZIP_CODE")
    private String zipCode;
    private String label;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private EntityUser user;

}

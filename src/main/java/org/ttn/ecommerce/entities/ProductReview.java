//package org.ttn.ecommerce.entities;
//
//import lombok.Data;
//import org.hibernate.validator.constraints.Range;
//import org.ttn.ecommerce.entities.register.Customer;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotNull;
//
//@Data
//@Entity
//public class ProductReview {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//    private String review;
//    @NotNull
//    @Range(min = 0, max = 10)
//    private Integer rating;
//
//    @OneToOne
//    private Customer customer;
//
//    @ManyToOne
//    private Product product;
//}

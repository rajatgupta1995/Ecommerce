//package org.ttn.ecommerce.entities;
//
//import lombok.Data;
//
//import javax.persistence.*;
//
//@Data
//@Entity
//public class ProductVariation {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//    @ManyToOne
//    @JoinColumn(name = "product_id")
//    private Product product;
//    private Integer quantityAvailable;
//    private Double price;
//    private String primaryImageName;
//    private Boolean isActive;
//
//}

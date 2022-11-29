//package org.ttn.ecommerce.entities;
//
//import java.util.List;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.OneToMany;
//import javax.validation.constraints.NotNull;
//import lombok.Data;
//import org.ttn.ecommerce.entities.ProductReview;
//import org.ttn.ecommerce.entities.ProductVariation;
//import org.ttn.ecommerce.entities.register.Seller;
//
//@Entity
//@Data
//public class Product {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "product_id")
//    private Integer id;
//
//    @ManyToOne
//    @JoinColumn(name = "seller_user_id")
//    private Seller seller;
//
//    @ManyToOne
//    @JoinColumn(name = "category_id")
//    private Category category;
//
//    private String description;
//
//    @NotNull(message = "Name should not be blank")
//    private String name;
//
//    @NotNull
//    private Boolean is_cancellable;
//    @NotNull
//    private Boolean is_returnable;
//    @NotNull
//    private String brand;
//    private boolean is_active;
//    private boolean is_deleted;
//
//    @OneToMany(mappedBy = "product")
//    private List<ProductVariation> productVariations;
//
//
//    @OneToMany(mappedBy = "product")
//    private List<ProductReview> productReviews;
//
//
//}
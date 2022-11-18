package com.Ecommerce.EcoProject.Model.register;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Role{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String authority;

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    private List<EntityUser> users;
}

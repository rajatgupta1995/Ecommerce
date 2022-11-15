package com.Ecommerce.EcoProject.Model.register;

import javax.persistence.*;
import java.util.List;

@Entity
public class Role{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String authority;

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    private List<EntityUser> users;

    //getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}

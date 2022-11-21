package org.ttn.ecommerce.entities.register;

import lombok.Data;
import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.entities.register.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="USER")
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class UserEntity {

    @Id
    @SequenceGenerator(name="user_sequence",sequenceName = "user_sequence",initialValue = 1,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "user_sequence")
    private Long id;

    @Column(name="Email")
    @Email
    @NotEmpty
    @NotNull
    private String email;


    private String firstName;

    private String middleName;


    private String lastName;


    private String password;

    @Transient
    private String ConfirmPassword;

    private boolean isActive;

    private boolean isLocked;

    private boolean isDeleted;

    private boolean isExpired;

    private int invalidAttemptCount;

    private Date passwordUpdateDate;



    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private Set<Address> addresses;




    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id"))
    private List<Role> roles;

 //   @OneToOne(mappedBy = "user")
   // private Token token;
}

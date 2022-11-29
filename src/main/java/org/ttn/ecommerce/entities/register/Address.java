package org.ttn.ecommerce.entities.register;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name="address")
public class Address {

    @Id
    @SequenceGenerator(name="address_sequence",sequenceName = "address_sequence",initialValue = 1,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "address_sequence")
    private Long id;

    //@NotBlank(message = "Address cannot be blank")
    private String addressLine;

    //@NotBlank(message = "City cannot be blank")
    private String city;

    //@NotBlank(message = "State cannot be blank")
    private String state;

    //@NotBlank(message = "Country cannot be blank")
    private String country;

    //@NotBlank(message = "Zip code cannot be blank")
    @Size(min = 6, max = 6, message = "It should be exact 6 digits")
    private String zipCode;

    //@NotBlank(message = "Label cannot be blank")
    private String label;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private UserEntity userEntity;


    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", addressLine='" + addressLine + '\'' +
                ",zipCode='" + zipCode + '\'' +
                ", label='" + label + '\'' +
                '}';
    }

}

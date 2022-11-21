package org.ttn.ecommerce.entities.register;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="roles")
@Getter
@Setter
@NoArgsConstructor
public class Role{

    @Id
    @SequenceGenerator(name="token_sequence",sequenceName = "token_sequence",initialValue = 1,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "token_sequence")
    private Long id;

    private String authority;



}


package org.ttn.ecommerce.entity.register;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ttn.ecommerce.controller.auditing.Auditable;

import javax.persistence.*;

@Entity
@Table(name="roles")
@Getter
@Setter
@NoArgsConstructor
public class Role extends Auditable<String>{

    @Id
    @SequenceGenerator(name="token_sequence",sequenceName = "token_sequence",initialValue = 1,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "token_sequence")
    private Long id;

    private String authority;



}


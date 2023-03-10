package org.ttn.ecommerce.repository.registerrepository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entity.register.Role;

import java.util.Optional;

@Repository
public interface RoleRepository  extends JpaRepository<Role,Long> {

    Optional<Role> findByAuthority(String authority);
}

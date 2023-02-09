package com.codeoftheweb.salvo.repository;

import com.codeoftheweb.salvo.models.ERole;
import com.codeoftheweb.salvo.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}

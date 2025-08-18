package com.example.BlogOnline.Repository;

import com.example.BlogOnline.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    public boolean existsByName(String name);
    public Optional<Role> findByName(String name);
    public void deleteAll();
}

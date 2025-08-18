package com.example.BlogOnline.Repository;

import com.example.BlogOnline.Model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, Long> {

    public boolean existsByName(String name);
    public Optional<Permission> findByName(String name);
    public void deleteAll();

}

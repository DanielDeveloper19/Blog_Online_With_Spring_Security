package com.example.BlogOnline.Repository;

import com.example.BlogOnline.Model.UserSec;
import com.example.BlogOnline.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSecRepo extends JpaRepository<UserSec, Long> {
    Optional<UserSec> findByUsername(String username);
    Optional<UserSec> findByEmail(String email);

    UserSec user(Usuario user);
}

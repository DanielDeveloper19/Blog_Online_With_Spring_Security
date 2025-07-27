package com.example.BlogOnline.Repository;

import com.example.BlogOnline.Model.UserSec;
import com.example.BlogOnline.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
   Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByUserSec(UserSec userSec);
}
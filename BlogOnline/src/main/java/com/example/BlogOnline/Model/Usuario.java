package com.example.BlogOnline.Model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;


    @OneToOne(cascade = CascadeType.ALL)
    private UserSec userSec;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Posteo> posteos = new HashSet<>();

    private boolean enabled= true ;


    // En Usuario.java
    @Override
    public int hashCode() {
        return Objects.hash(id); // Solo usar ID, no userSec ni posteos
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return Objects.equals(id, usuario.id);
    }


}

package com.example.BlogOnline.Controllers;

import com.example.BlogOnline.DTO.UsuarioDto;
import com.example.BlogOnline.Service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/usuarios/getAll")
    public ResponseEntity<List<UsuarioDto>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> getUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> createUsuario(@RequestBody UsuarioDto usuarioDto) {
        return ResponseEntity.ok(usuarioService.createUsuario(usuarioDto));
    }

    @PutMapping("/usuarios/update")
    public ResponseEntity<UsuarioDto> updateUsuario(@RequestBody UsuarioDto usuarioDto, Authentication authentication) {
        return ResponseEntity.ok(usuarioService.updateUsuario(authentication.getName(), usuarioDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")//Listo
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
package com.example.BlogOnline.Controllers;

import com.example.BlogOnline.DTO.PosteoDTO;
import com.example.BlogOnline.DTO.PosteoRequestDTO;
import com.example.BlogOnline.Service.impl.PosteoServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PosteoController { //Listo

     @Autowired
    private  PosteoServiceImpl posteoService;

    // Crear nuevo posteo (usuario extraído del token)
    //LISTO
    @PostMapping(value = "/posteo/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PosteoDTO> createPosteo(@Valid @RequestBody PosteoRequestDTO posteoRequestDTO,
                                                  Authentication authentication) {
        try {
            String username = authentication.getName(); // Obtiene el username del token

            System.out.println("Username del token: " + username);

            PosteoDTO createdPosteo = posteoService.createPosteo(posteoRequestDTO, username);
            System.out.println("created posteo");
            return new ResponseEntity<>(createdPosteo, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());

            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener todos los posteos
    @GetMapping("/posteo/getAll")
    public ResponseEntity<List<PosteoDTO>> getAllPosteos() {
        List<PosteoDTO> posteos = posteoService.getAllPosteos();
        return new ResponseEntity<>(posteos, HttpStatus.OK);
    }

    // Obtener mis posteos (del usuario autenticado)
    //lISTO
    @GetMapping("/posteos/myPosts")
    public ResponseEntity<List<PosteoDTO>> getMyPosteos(Authentication authentication) {

        System.out.println("ingresando al endpoint de mis post");
        System.out.println("Username del token: " + authentication.getName());

        String username = authentication.getName();
        List<PosteoDTO> posteos = posteoService.getMyPosteos(username);
        return new ResponseEntity<>(posteos, HttpStatus.OK);
    }

    // Obtener posteo por ID
    @GetMapping("/posteo/{id}")
    public ResponseEntity<PosteoDTO> getPosteoById(@PathVariable Long id) {
        Optional<PosteoDTO> posteo = posteoService.getPosteoById(id);
        return posteo.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Obtener posteos por usuario (endpoint público)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PosteoDTO>> getPosteosByUserId(@PathVariable Long userId) {
        List<PosteoDTO> posteos = posteoService.getPosteosByUserId(userId);
        return new ResponseEntity<>(posteos, HttpStatus.OK);
    }

    // Actualizar posteo (solo el propietario puede hacerlo)
    @PutMapping("posteos/update/{id}")
    public ResponseEntity<PosteoDTO> updatePosteo(@PathVariable Long id,
                                                  @Valid @RequestBody PosteoRequestDTO posteoRequestDTO,
                                                  Authentication authentication) {
        try {
            String username = authentication.getName();
            PosteoDTO updatedPosteo = posteoService.updatePosteo(id, posteoRequestDTO, username);
            return new ResponseEntity<>(updatedPosteo, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    // Eliminar posteo (solo el propietario puede hacerlo)
    // LISTO
    @DeleteMapping("/posteos/delete/{id}")
    public ResponseEntity<Void> deletePosteo(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            posteoService.deletePosteo(id, username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Verificar si existe un posteo
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsPosteo(@PathVariable Long id) {
        boolean exists = posteoService.existsById(id);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
}

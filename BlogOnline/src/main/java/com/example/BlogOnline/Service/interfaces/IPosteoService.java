package com.example.BlogOnline.Service.interfaces;

import com.example.BlogOnline.DTO.PosteoDTO;
import com.example.BlogOnline.DTO.PosteoRequestDTO;

import java.util.List;
import java.util.Optional;

public interface IPosteoService {

    // Crear un nuevo posteo usando el usuario autenticado
    PosteoDTO createPosteo(PosteoRequestDTO posteoRequestDTO, String username);


    List<PosteoDTO> getAllPosteos();


    Optional<PosteoDTO> getPosteoById(Long id);


    List<PosteoDTO> getPosteosByUserId(Long userId);

    // Obtener posteos del usuario autenticado
    List<PosteoDTO> getMyPosteos(String username);

    // Actualizar posteo (solo si pertenece al usuario autenticado)
    PosteoDTO updatePosteo(Long id, PosteoRequestDTO posteoRequestDTO, String username);

    // Eliminar posteo (solo si pertenece al usuario autenticado)
    void deletePosteo(Long id, String username);


    boolean existsById(Long id);

}

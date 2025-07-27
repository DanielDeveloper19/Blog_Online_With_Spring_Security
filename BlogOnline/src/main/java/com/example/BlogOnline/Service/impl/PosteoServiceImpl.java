package com.example.BlogOnline.Service.impl;


import com.example.BlogOnline.DTO.PosteoDTO;
import com.example.BlogOnline.DTO.PosteoRequestDTO;
import com.example.BlogOnline.Model.Posteo;
import com.example.BlogOnline.Model.UserSec;
import com.example.BlogOnline.Model.Usuario;
import com.example.BlogOnline.Repository.PosteoRepository;
import com.example.BlogOnline.Repository.UserSecRepo;
import com.example.BlogOnline.Repository.UsuarioRepository;
import com.example.BlogOnline.Service.interfaces.IPosteoService;
import com.example.BlogOnline.mapeos.IPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PosteoServiceImpl implements IPosteoService {//Listo

    @Autowired
    private PosteoRepository posteoRepository;

@Autowired
    private UserSecRepo userSecRepo;

@Autowired
private UsuarioRepository usuarioRepository;

@Autowired
private IPostMapper postMapper;

    @Override
    public PosteoDTO createPosteo(PosteoRequestDTO posteoRequestDTO, String username) {

        System.out.println("createPosteo del Service");
        // Buscar el userSec por username (extraído del token)
        UserSec userSec =  userSecRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        Usuario user = (Usuario) usuarioRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario por UserSEC no encontrado: " + username));

        Posteo posteo = new Posteo();
        posteo.setUser(user);
        posteo.setContent(posteoRequestDTO.getContent());


        user.getPosteos().add(posteo);


        System.out.println("Posteo casi guardado");
        Posteo savedPosteo = posteoRepository.save(posteo);

        return postMapper.convertToDTO(savedPosteo);

    }//Listo

    @Override
    @Transactional(readOnly = true)
    public List<PosteoDTO> getAllPosteos() {
        return posteoRepository.findAll().stream()
                .map(postMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PosteoDTO> getPosteoById(Long id) {
        return posteoRepository.findById(id)
                .map(postMapper::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PosteoDTO> getPosteosByUserId(Long userId) {
        return posteoRepository.findByUserId(userId).stream()
                .map(postMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PosteoDTO> getMyPosteos(String username) {
        Usuario usuario = (Usuario) usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        return posteoRepository.findByUserId(usuario.getId()).stream()
                .map(postMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PosteoDTO updatePosteo(Long id, PosteoRequestDTO posteoRequestDTO, String username) {
        Posteo existingPosteo = posteoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posteo no encontrado con ID: " + id));

        // Verificar que el posteo pertenece al usuario autenticado
        if (!existingPosteo.getUser().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permisos para editar este posteo");
        }

        if (posteoRequestDTO.getContent() != null) {
            existingPosteo.setContent(posteoRequestDTO.getContent());
        }
        if (posteoRequestDTO.getEnabled() != null) {
            existingPosteo.setEnabled(posteoRequestDTO.getEnabled());
        }

        Posteo updatedPosteo = posteoRepository.save(existingPosteo);

  return postMapper.convertToDTO(updatedPosteo);
    }//Listo

    @Override
    public void deletePosteo(Long id, String username) {
        Posteo posteo = posteoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posteo no encontrado con ID: " + id));

        // Verificar que el posteo pertenece al usuario autenticado
        if (!posteo.getUser().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permisos para eliminar este posteo");
        }

           posteo.setEnabled(false);
        posteoRepository.save(posteo);
    }//Listo


    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return posteoRepository.existsById(id);
    }


}

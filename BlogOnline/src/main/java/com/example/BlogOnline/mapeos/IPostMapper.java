package com.example.BlogOnline.mapeos;


import com.example.BlogOnline.DTO.PosteoDTO;
import com.example.BlogOnline.Model.Posteo;

import com.example.BlogOnline.Repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IPostMapper {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public PosteoDTO convertToDTO(Posteo posteo){
        PosteoDTO posteoDTO = new PosteoDTO();
        posteoDTO.setId(posteo.getId());
        posteoDTO.setContent(posteo.getContent());
        posteoDTO.setUserName(posteo.getUser().getUsername());
        posteoDTO.setEnabled(posteo.isEnabled());
        return posteoDTO;
    }

    public Posteo convertToEntity(PosteoDTO posteoDTO){

        Posteo posteo = new Posteo();
        posteo.setId(posteoDTO.getId());
        posteo.setContent(posteoDTO.getContent());
        posteo.setUser(usuarioRepository.findByUsername(posteoDTO.getUserName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + posteoDTO.getUserName())));

        posteo.setEnabled(posteoDTO.isEnabled());
return  posteo;
    }


}
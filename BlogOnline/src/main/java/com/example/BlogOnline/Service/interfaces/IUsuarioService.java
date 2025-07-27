package com.example.BlogOnline.Service.interfaces;

import com.example.BlogOnline.DTO.UsuarioDto;

import java.util.List;

public interface IUsuarioService {
    List<UsuarioDto> getAllUsuarios();
    UsuarioDto getUsuarioById(Long id);
    UsuarioDto createUsuario(UsuarioDto usuarioDto);
    UsuarioDto updateUsuario(Long id, UsuarioDto usuarioDto);
    void deleteUsuario(Long id);
}
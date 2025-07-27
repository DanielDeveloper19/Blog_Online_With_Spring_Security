package com.example.BlogOnline.Service.impl;

import com.example.BlogOnline.DTO.UsuarioDto;
import com.example.BlogOnline.Model.Permission;
import com.example.BlogOnline.Model.Usuario;
import com.example.BlogOnline.Repository.UsuarioRepository;
import com.example.BlogOnline.Service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<UsuarioDto> getAllUsuarios() {
        return usuarioRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UsuarioDto getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario not found"));
        return convertToDto(usuario);
    }

    @Override
    public UsuarioDto createUsuario(UsuarioDto usuarioDto) {
        Usuario usuario = convertToEntity(usuarioDto);
        return convertToDto(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioDto updateUsuario(String name, UsuarioDto usuarioDto) {
        Usuario existingUsuario = usuarioRepository.findByUsername(name).orElseThrow(() -> new RuntimeException("Usuario not found"));
        existingUsuario.setUsername(usuarioDto.getUsername());

        return convertToDto(usuarioRepository.save(existingUsuario));
    }

    @Override
    public void deleteUsuario(Long id) {

        Usuario usuario = usuarioRepository.getById(id);
        usuario.setEnabled(false);

        usuarioRepository.save(usuario);
    }

    private UsuarioDto convertToDto(Usuario usuario) {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setUsername(usuario.getUsername());
        usuarioDto.setEnabled(usuario.isEnabled());
        return usuarioDto;
    }

    private Usuario convertToEntity(UsuarioDto usuarioDto) {
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioDto.getUsername());
        usuario.setEnabled(usuarioDto.isEnabled());
        return usuario;
    }
}
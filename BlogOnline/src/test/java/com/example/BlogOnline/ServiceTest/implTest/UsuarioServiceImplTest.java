package com.example.BlogOnline.ServiceTest.implTest;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.stream.Collectors;

import com.example.BlogOnline.DTO.UsuarioDto;
import com.example.BlogOnline.Model.Posteo;
import com.example.BlogOnline.Model.Usuario;
import com.example.BlogOnline.Repository.UsuarioRepository;
import com.example.BlogOnline.Service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private UsuarioDto usuarioDto;
    private Posteo posteo;

    @BeforeEach
    void setUp() {
        // Setup Usuario entity
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setEnabled(true);

        // Setup Posteo entity
        posteo = new Posteo();
        posteo.setId(1L);
        posteo.setContent("Test content");
        posteo.setUser(usuario);
        posteo.setEnabled(true);

        // Setup Usuario with Posteos
        Set<Posteo> posteos = new HashSet<>();
        posteos.add(posteo);
        usuario.setPosteos(posteos);

        // Setup UsuarioDto
        usuarioDto = new UsuarioDto();
        usuarioDto.setId(1L);
        usuarioDto.setUsername("testuser");
        usuarioDto.setEnabled(true);
    }

    @Test
    void getAllUsuarios_ShouldReturnListOfUsuarioDtos() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<UsuarioDto> result = usuarioService.getAllUsuarios();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(usuario.getId(), result.get(0).getId());
        assertEquals(usuario.getUsername(), result.get(0).getUsername());
        assertEquals(usuario.isEnabled(), result.get(0).isEnabled());
        assertNotNull(result.get(0).getPosteos());
        assertEquals(1, result.get(0).getPosteos().size());

        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void getAllUsuarios_ShouldReturnEmptyList_WhenNoUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UsuarioDto> result = usuarioService.getAllUsuarios();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void getUsuarioById_ShouldReturnUsuarioDto_WhenUsuarioExists() {
        // Arrange
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        // Act
        UsuarioDto result = usuarioService.getUsuarioById(id);

        // Assert
        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());
        assertEquals(usuario.getUsername(), result.getUsername());
        assertEquals(usuario.isEnabled(), result.isEnabled());
        verify(usuarioRepository, times(1)).findById(id);
    }

    @Test
    void getUsuarioById_ShouldThrowException_WhenUsuarioNotFound() {
        // Arrange
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.getUsuarioById(id));

        assertEquals("Usuario not found", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(id);
    }

    @Test
    void createUsuario_ShouldReturnCreatedUsuarioDto() {
        // Arrange
        UsuarioDto inputDto = new UsuarioDto();
        inputDto.setUsername("newuser");
        inputDto.setEnabled(true);

        Usuario savedUsuario = new Usuario();
        savedUsuario.setId(2L);
        savedUsuario.setUsername("newuser");
        savedUsuario.setEnabled(true);
        savedUsuario.setPosteos(new HashSet<>());

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUsuario);

        // Act
        UsuarioDto result = usuarioService.createUsuario(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(savedUsuario.getId(), result.getId());
        assertEquals(savedUsuario.getUsername(), result.getUsername());
        assertEquals(savedUsuario.isEnabled(), result.isEnabled());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void updateUsuario_ShouldReturnUpdatedUsuarioDto_WhenUsuarioExists() {
        // Arrange
        String username = "testuser";
        UsuarioDto updateDto = new UsuarioDto();
        updateDto.setUsername("updateduser");

        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(1L);
        existingUsuario.setUsername("testuser");
        existingUsuario.setEnabled(true);
        existingUsuario.setPosteos(new HashSet<>());

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setId(1L);
        updatedUsuario.setUsername("updateduser");
        updatedUsuario.setEnabled(true);
        updatedUsuario.setPosteos(new HashSet<>());

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.save(existingUsuario)).thenReturn(updatedUsuario);

        // Act
        UsuarioDto result = usuarioService.updateUsuario(username, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(updatedUsuario.getId(), result.getId());
        assertEquals("updateduser", result.getUsername());
        assertEquals(updatedUsuario.isEnabled(), result.isEnabled());
        verify(usuarioRepository, times(1)).findByUsername(username);
        verify(usuarioRepository, times(1)).save(existingUsuario);
    }

    @Test
    void updateUsuario_ShouldThrowException_WhenUsuarioNotFound() {
        // Arrange
        String username = "nonexistent";
        UsuarioDto updateDto = new UsuarioDto();
        updateDto.setUsername("updateduser");

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.updateUsuario(username, updateDto));

        assertEquals("Usuario not found", exception.getMessage());
        verify(usuarioRepository, times(1)).findByUsername(username);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deleteUsuario_ShouldDisableUsuario() {
        // Arrange
        Long id = 1L;
        Usuario usuarioToDelete = new Usuario();
        usuarioToDelete.setId(id);
        usuarioToDelete.setUsername("testuser");
        usuarioToDelete.setEnabled(true);

        when(usuarioRepository.getById(id)).thenReturn(usuarioToDelete);

        // Act
        usuarioService.deleteUsuario(id);

        // Assert
        assertFalse(usuarioToDelete.isEnabled());
        verify(usuarioRepository, times(1)).getById(id);
        verify(usuarioRepository, times(1)).save(usuarioToDelete);
    }

    @Test
    void convertToDto_ShouldConvertUsuarioWithoutPosteos() {
        // Arrange
        Usuario usuarioSinPosteos = new Usuario();
        usuarioSinPosteos.setId(1L);
        usuarioSinPosteos.setUsername("testuser");
        usuarioSinPosteos.setEnabled(true);
        usuarioSinPosteos.setPosteos(null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioSinPosteos));

        // Act
        UsuarioDto result = usuarioService.getUsuarioById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(usuarioSinPosteos.getId(), result.getId());
        assertEquals(usuarioSinPosteos.getUsername(), result.getUsername());
        assertEquals(usuarioSinPosteos.isEnabled(), result.isEnabled());
        assertNull(result.getPosteos());
    }

    @Test
    void convertToDto_ShouldConvertUsuarioWithEmptyPosteos() {
        // Arrange
        Usuario usuarioConPosteosVacios = new Usuario();
        usuarioConPosteosVacios.setId(1L);
        usuarioConPosteosVacios.setUsername("testuser");
        usuarioConPosteosVacios.setEnabled(true);
        usuarioConPosteosVacios.setPosteos(new HashSet<>());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioConPosteosVacios));

        // Act
        UsuarioDto result = usuarioService.getUsuarioById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(usuarioConPosteosVacios.getId(), result.getId());
        assertEquals(usuarioConPosteosVacios.getUsername(), result.getUsername());
        assertEquals(usuarioConPosteosVacios.isEnabled(), result.isEnabled());
        assertNull(result.getPosteos());
    }

    @Test
    void convertToEntity_ShouldConvertUsuarioDtoToUsuario() {
        // Arrange
        UsuarioDto dto = new UsuarioDto();
        dto.setUsername("testuser");
        dto.setEnabled(true);

        Usuario savedUsuario = new Usuario();
        savedUsuario.setId(1L);
        savedUsuario.setUsername("testuser");
        savedUsuario.setEnabled(true);
        savedUsuario.setPosteos(new HashSet<>());

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUsuario);

        // Act
        UsuarioDto result = usuarioService.createUsuario(dto);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertTrue(result.isEnabled());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}

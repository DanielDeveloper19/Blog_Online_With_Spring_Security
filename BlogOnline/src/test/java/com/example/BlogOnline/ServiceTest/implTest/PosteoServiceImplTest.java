package com.example.BlogOnline.ServiceTest.implTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.example.BlogOnline.DTO.PosteoDTO;
import com.example.BlogOnline.DTO.PosteoRequestDTO;
import com.example.BlogOnline.Model.Posteo;
import com.example.BlogOnline.Model.UserSec;
import com.example.BlogOnline.Model.Usuario;
import com.example.BlogOnline.Repository.PosteoRepository;
import com.example.BlogOnline.Repository.UserSecRepo;
import com.example.BlogOnline.Repository.UsuarioRepository;
import com.example.BlogOnline.Service.impl.PosteoServiceImpl;
import com.example.BlogOnline.mapeos.IPostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;




@ExtendWith(MockitoExtension.class)
class PosteoServiceImplTest {

    @Mock
    private PosteoRepository posteoRepository;

    @Mock
    private UserSecRepo userSecRepo;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private IPostMapper postMapper;

    @InjectMocks
    private PosteoServiceImpl posteoService;

    private Usuario testUsuario;
    private UserSec testUserSec;
    private Posteo testPosteo;
    private PosteoDTO testPosteoDTO;
    private PosteoRequestDTO testPosteoRequestDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        testUsuario = new Usuario();
        testUsuario.setId(1L);
        testUsuario.setUsername("testuser");
        testUsuario.setPosteos(new HashSet<Posteo>() {
        });

        testUserSec = new UserSec();
        testUserSec.setUsername("testuser");

        testPosteo = new Posteo();
        testPosteo.setId(1L);
        testPosteo.setContent("Test content");
        testPosteo.setUser(testUsuario);
        testPosteo.setEnabled(true);

        testPosteoDTO = new PosteoDTO();
        testPosteoDTO.setId(1L);
        testPosteoDTO.setContent("Test content");

        testPosteoRequestDTO = new PosteoRequestDTO();
        testPosteoRequestDTO.setContent("Test content");
    }

    @Test
    void createPosteo_Success() {
        // Given
        String username = "testuser";
        when(userSecRepo.findByUsername(username)).thenReturn(Optional.of(testUserSec));
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(testUsuario));
        when(posteoRepository.save(any(Posteo.class))).thenReturn(testPosteo);
        when(postMapper.convertToDTO(testPosteo)).thenReturn(testPosteoDTO);

        // When
        PosteoDTO result = posteoService.createPosteo(testPosteoRequestDTO, username);

        // Then
        assertNotNull(result);
        assertEquals(testPosteoDTO.getId(), result.getId());
        assertEquals(testPosteoDTO.getContent(), result.getContent());

        verify(userSecRepo).findByUsername(username);
        verify(usuarioRepository).findByUsername(username);
        verify(posteoRepository).save(any(Posteo.class));
        verify(postMapper).convertToDTO(testPosteo);
    }

    @Test
    void createPosteo_UserSecNotFound_ThrowsException() {
        // Given
        String username = "nonexistent";
        when(userSecRepo.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> posteoService.createPosteo(testPosteoRequestDTO, username));

        assertEquals("Usuario no encontrado: " + username, exception.getMessage());
        verify(userSecRepo).findByUsername(username);
        verifyNoInteractions(usuarioRepository, posteoRepository, postMapper);
    }

    @Test
    void createPosteo_UsuarioNotFound_ThrowsException() {
        // Given
        String username = "testuser";
        when(userSecRepo.findByUsername(username)).thenReturn(Optional.of(testUserSec));
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> posteoService.createPosteo(testPosteoRequestDTO, username));

        assertEquals("Usuario por UserSEC no encontrado: " + username, exception.getMessage());
        verify(userSecRepo).findByUsername(username);
        verify(usuarioRepository).findByUsername(username);
        verifyNoInteractions(posteoRepository, postMapper);
    }

    @Test
    void getAllPosteos_Success() {
        // Given
        List<Posteo> posteos = Arrays.asList(testPosteo);
        List<PosteoDTO> posteoDTOs = Arrays.asList(testPosteoDTO);

        when(posteoRepository.findAll()).thenReturn(posteos);
        when(postMapper.convertToDTO(testPosteo)).thenReturn(testPosteoDTO);

        // When
        List<PosteoDTO> result = posteoService.getAllPosteos();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPosteoDTO.getId(), result.get(0).getId());

        verify(posteoRepository).findAll();
        verify(postMapper).convertToDTO(testPosteo);
    }

    @Test
    void getPosteoById_Found_ReturnsPosteoDTO() {
        // Given
        Long id = 1L;
        when(posteoRepository.findById(id)).thenReturn(Optional.of(testPosteo));
        when(postMapper.convertToDTO(testPosteo)).thenReturn(testPosteoDTO);

        // When
        Optional<PosteoDTO> result = posteoService.getPosteoById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testPosteoDTO.getId(), result.get().getId());

        verify(posteoRepository).findById(id);
        verify(postMapper).convertToDTO(testPosteo);
    }

    @Test
    void getPosteoById_NotFound_ReturnsEmpty() {
        // Given
        Long id = 1L;
        when(posteoRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<PosteoDTO> result = posteoService.getPosteoById(id);

        // Then
        assertTrue(result.isEmpty());

        verify(posteoRepository).findById(id);
        verifyNoInteractions(postMapper);
    }

    @Test
    void getPosteosByUserId_Success() {
        // Given
        Long userId = 1L;
        List<Posteo> posteos = Arrays.asList(testPosteo);

        when(posteoRepository.findByUserId(userId)).thenReturn(posteos);
        when(postMapper.convertToDTO(testPosteo)).thenReturn(testPosteoDTO);

        // When
        List<PosteoDTO> result = posteoService.getPosteosByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPosteoDTO.getId(), result.get(0).getId());

        verify(posteoRepository).findByUserId(userId);
        verify(postMapper).convertToDTO(testPosteo);
    }

    @Test
    void getMyPosteos_Success() {
        // Given
        String username = "testuser";
        List<Posteo> posteos = Arrays.asList(testPosteo);

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(testUsuario));
        when(posteoRepository.findByUserId(testUsuario.getId())).thenReturn(posteos);
        when(postMapper.convertToDTO(testPosteo)).thenReturn(testPosteoDTO);

        // When
        List<PosteoDTO> result = posteoService.getMyPosteos(username);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPosteoDTO.getId(), result.get(0).getId());

        verify(usuarioRepository).findByUsername(username);
        verify(posteoRepository).findByUserId(testUsuario.getId());
        verify(postMapper).convertToDTO(testPosteo);
    }

    @Test
    void getMyPosteos_UserNotFound_ThrowsException() {
        // Given
        String username = "nonexistent";
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> posteoService.getMyPosteos(username));

        assertEquals("Usuario no encontrado: " + username, exception.getMessage());
        verify(usuarioRepository).findByUsername(username);
        verifyNoInteractions(posteoRepository, postMapper);
    }

    @Test
    void updatePosteo_Success() {
        // Given
        Long id = 1L;
        String username = "testuser";
        PosteoRequestDTO updateRequest = new PosteoRequestDTO();
        updateRequest.setContent("Updated content");
        updateRequest.setEnabled(false);

        when(posteoRepository.findById(id)).thenReturn(Optional.of(testPosteo));
        when(posteoRepository.save(testPosteo)).thenReturn(testPosteo);
        when(postMapper.convertToDTO(testPosteo)).thenReturn(testPosteoDTO);

        // When
        PosteoDTO result = posteoService.updatePosteo(id, updateRequest, username);

        // Then
        assertNotNull(result);
        assertEquals("Updated content", testPosteo.getContent());
        assertEquals(false, testPosteo.isEnabled());

        verify(posteoRepository).findById(id);
        verify(posteoRepository).save(testPosteo);
        verify(postMapper).convertToDTO(testPosteo);
    }

    @Test
    void updatePosteo_PosteoNotFound_ThrowsException() {
        // Given
        Long id = 1L;
        String username = "testuser";
        when(posteoRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> posteoService.updatePosteo(id, testPosteoRequestDTO, username));

        assertEquals("Posteo no encontrado con ID: " + id, exception.getMessage());
        verify(posteoRepository).findById(id);
        verifyNoMoreInteractions(posteoRepository);
        verifyNoInteractions(postMapper);
    }

    @Test
    void updatePosteo_UnauthorizedUser_ThrowsException() {
        // Given
        Long id = 1L;
        String username = "otheruser";
        when(posteoRepository.findById(id)).thenReturn(Optional.of(testPosteo));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> posteoService.updatePosteo(id, testPosteoRequestDTO, username));

        assertEquals("No tienes permisos para editar este posteo", exception.getMessage());
        verify(posteoRepository).findById(id);
        verifyNoMoreInteractions(posteoRepository);
        verifyNoInteractions(postMapper);
    }

    @Test
    void updatePosteo_OnlyContentUpdate() {
        // Given
        Long id = 1L;
        String username = "testuser";
        PosteoRequestDTO updateRequest = new PosteoRequestDTO();
        updateRequest.setContent("Updated content only");
        // enabled es null

        when(posteoRepository.findById(id)).thenReturn(Optional.of(testPosteo));
        when(posteoRepository.save(testPosteo)).thenReturn(testPosteo);
        when(postMapper.convertToDTO(testPosteo)).thenReturn(testPosteoDTO);

        // When
        PosteoDTO result = posteoService.updatePosteo(id, updateRequest, username);

        // Then
        assertNotNull(result);
        assertEquals("Updated content only", testPosteo.getContent());
        // enabled should remain true (original value)
        assertTrue(testPosteo.isEnabled());

        verify(posteoRepository).findById(id);
        verify(posteoRepository).save(testPosteo);
        verify(postMapper).convertToDTO(testPosteo);
    }

    @Test
    void deletePosteo_Success() {
        // Given
        Long id = 1L;
        String username = "testuser";
        when(posteoRepository.findById(id)).thenReturn(Optional.of(testPosteo));
        when(posteoRepository.save(testPosteo)).thenReturn(testPosteo);

        // When
        posteoService.deletePosteo(id, username);

        // Then
        assertFalse(testPosteo.isEnabled());
        verify(posteoRepository).findById(id);
        verify(posteoRepository).save(testPosteo);
    }

    @Test
    void deletePosteo_PosteoNotFound_ThrowsException() {
        // Given
        Long id = 1L;
        String username = "testuser";
        when(posteoRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> posteoService.deletePosteo(id, username));

        assertEquals("Posteo no encontrado con ID: " + id, exception.getMessage());
        verify(posteoRepository).findById(id);
        verifyNoMoreInteractions(posteoRepository);
    }

    @Test
    void deletePosteo_UnauthorizedUser_ThrowsException() {
        // Given
        Long id = 1L;
        String username = "otheruser";
        when(posteoRepository.findById(id)).thenReturn(Optional.of(testPosteo));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> posteoService.deletePosteo(id, username));

        assertEquals("No tienes permisos para eliminar este posteo", exception.getMessage());
        verify(posteoRepository).findById(id);
        verifyNoMoreInteractions(posteoRepository);
    }

    @Test
    void existsById_True() {
        // Given
        Long id = 1L;
        when(posteoRepository.existsById(id)).thenReturn(true);

        // When
        boolean result = posteoService.existsById(id);

        // Then
        assertTrue(result);
        verify(posteoRepository).existsById(id);
    }

    @Test
    void existsById_False() {
        // Given
        Long id = 1L;
        when(posteoRepository.existsById(id)).thenReturn(false);

        // When
        boolean result = posteoService.existsById(id);

        // Then
        assertFalse(result);
        verify(posteoRepository).existsById(id);
    }
}

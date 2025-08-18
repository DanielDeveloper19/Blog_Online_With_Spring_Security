package com.example.BlogOnline.controllerTest;

import com.example.BlogOnline.DTO.PosteoRequestDTO;
import com.example.BlogOnline.Model.Posteo;
import com.example.BlogOnline.Model.UserSec;
import com.example.BlogOnline.Model.Usuario;
import com.example.BlogOnline.Repository.PosteoRepository;
import com.example.BlogOnline.Repository.UserSecRepo;
import com.example.BlogOnline.Repository.UsuarioRepository;
import com.example.BlogOnline.testConfig.MySQLTestContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PosteoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PosteoRepository posteoRepository;

    @Autowired
    private UserSecRepo userSecRepo;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario user1;
    private Usuario user2;

    static {
        MySQLContainer<?> mysql = MySQLTestContainer.getInstance();
        System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysql.getUsername());
        System.setProperty("spring.datasource.password", mysql.getPassword());
    }

    @BeforeEach
    void setUp() {
        posteoRepository.deleteAll();
        usuarioRepository.deleteAll();
        userSecRepo.deleteAll();

        user1 = createUser("user1");
        user2 = createUser("user2");
    }

    private Usuario createUser(String username) {
        UserSec userSec = new UserSec();
        userSec.setUsername(username);
        userSec.setPassword("password");
        userSec.setRoles(new HashSet<>());

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setUserSec(userSec);
        userSec.setUser(usuario);

        return usuarioRepository.save(usuario);
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreatePosteo() throws Exception {
        PosteoRequestDTO requestDTO = new PosteoRequestDTO();
        requestDTO.setContent("Contenido del post");

        mockMvc.perform(post("/posteo/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is("Contenido del post")))
                .andExpect(jsonPath("$.nombreUsuario", is("user1")));
    }

    @Test
    void testGetAllPosteos() throws Exception {
        Posteo post1 = new Posteo();
        post1.setContent("Contenido 1");
        post1.setUser(user1);

        Posteo post2 = new Posteo();
        post2.setContent("Contenido 2");
        post2.setUser(user2);

        posteoRepository.saveAll(Arrays.asList(post1, post2));

        mockMvc.perform(get("/posteo/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetMyPosteos() throws Exception {
        Posteo post1 = new Posteo();
        post1.setContent("Mi Post");
        post1.setUser(user1);
        posteoRepository.save(post1);

        Posteo post2 = new Posteo();
        post2.setContent("Post de otro usuario");
        post2.setUser(user2);
        posteoRepository.save(post2);

        mockMvc.perform(get("/posteos/myPosts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content", is("Mi Post")));
    }

    @Test
    void testGetPosteoById() throws Exception {
        Posteo post = new Posteo();
        post.setContent("Contenido de prueba");
        post.setUser(user1);
        Posteo savedPost = posteoRepository.save(post);

        mockMvc.perform(get("/posteo/{id}", savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Contenido de prueba")));
    }

    @Test
    void testGetPosteosByUserId() throws Exception {
        Posteo post1 = new Posteo();
        post1.setContent("Post 1 de User1");
        post1.setUser(user1);
        posteoRepository.save(post1);

        mockMvc.perform(get("/user/{userId}", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdatePosteo_Success() throws Exception {
        Posteo post = new Posteo();
        post.setContent("Contenido Original");
        post.setUser(user1);
        Posteo savedPost = posteoRepository.save(post);

        PosteoRequestDTO requestDTO = new PosteoRequestDTO();
        requestDTO.setContent("Contenido Actualizado");

        mockMvc.perform(put("/posteos/update/{id}", savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Contenido Actualizado")));
    }

    @Test
    @WithMockUser(username = "user2")
    void testUpdatePosteo_Forbidden() throws Exception {
        Posteo post = new Posteo();
        post.setContent("Contenido de User1");
        post.setUser(user1);
        Posteo savedPost = posteoRepository.save(post);

        PosteoRequestDTO requestDTO = new PosteoRequestDTO();
        requestDTO.setContent("Intento de actualizacion");

        mockMvc.perform(put("/posteos/update/{id}", savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeletePosteo_Success() throws Exception {
        Posteo post = new Posteo();
        post.setContent("Contenido para borrar");
        post.setUser(user1);
        Posteo savedPost = posteoRepository.save(post);

        mockMvc.perform(delete("/posteos/delete/{id}", savedPost.getId()))
                .andExpect(status().isNoContent());

        assertFalse(posteoRepository.findById(savedPost.getId()).isPresent());
    }

    @Test
    @WithMockUser(username = "user2")
    void testDeletePosteo_Forbidden() throws Exception {
        Posteo post = new Posteo();
        post.setContent("Contenido de User1");
        post.setUser(user1);
        Posteo savedPost = posteoRepository.save(post);

        mockMvc.perform(delete("/posteos/delete/{id}", savedPost.getId()))
                .andExpect(status().isForbidden());
    }
}

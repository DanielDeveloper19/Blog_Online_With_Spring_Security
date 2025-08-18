package com.example.BlogOnline.controllerTest;

import com.example.BlogOnline.DTO.RoleDTO;
import com.example.BlogOnline.DTO.UserSecDTO;
import com.example.BlogOnline.Model.Role;
import com.example.BlogOnline.Model.UserSec;
import com.example.BlogOnline.Model.Usuario;
import com.example.BlogOnline.Repository.RoleRepository;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserSecControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserSecRepo userSecRepo;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role adminRole;
    private Role userRole;

    static {
        MySQLContainer<?> mysql = MySQLTestContainer.getInstance();
        System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysql.getUsername());
        System.setProperty("spring.datasource.password", mysql.getPassword());
    }

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        userSecRepo.deleteAll();
        roleRepository.deleteAll();

        adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        userRole = roleRepository.save(Role.builder().name("USER").build());
    }

    private UserSec createUser(String username, Set<Role> roles) {
        UserSec userSec = new UserSec();
        userSec.setUsername(username);
        userSec.setPassword("password");
        userSec.setEmail(username + "@example.com");
        userSec.setRoles(roles);

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setUserSec(userSec);
        userSec.setUser(usuario);

        usuarioRepository.save(usuario);
        return userSec;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser() throws Exception {
        RoleDTO userRoleDto = RoleDTO.builder().id(userRole.getId()).name(userRole.getName()).build();

        UserSecDTO newUserDto = UserSecDTO.builder()
                .username("newUser")
                .password("newPassword")
                .email("newuser@example.com")
                .roles(Collections.singleton(userRoleDto))
                .build();

        mockMvc.perform(post("/userSec/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newUser")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        createUser("user1", Collections.singleton(userRole));
        createUser("user2", new HashSet<>(List.of(userRole, adminRole)));

        mockMvc.perform(get("/userSec/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() throws Exception {
        UserSec user = createUser("testuser", Collections.singleton(userRole));

        mockMvc.perform(get("/userSec/getById/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser() throws Exception {
        UserSec user = createUser("userToUpdate", Collections.singleton(userRole));

        RoleDTO adminRoleDto = RoleDTO.builder().id(adminRole.getId()).name(adminRole.getName()).build();

        UserSecDTO updatedDto = UserSecDTO.builder()
                .id(user.getId())
                .username("updatedUser")
                .email("updated@example.com")
                .roles(Collections.singleton(adminRoleDto))
                .build();

        mockMvc.perform(put("/userSec/update/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updatedUser")))
                .andExpect(jsonPath("$.roles[0].name", is("ADMIN")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser() throws Exception {
        UserSec user = createUser("userToDelete", Collections.singleton(userRole));

        mockMvc.perform(delete("/userSec/delete/{id}", user.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userSecRepo.findById(user.getId()).isPresent());
        assertFalse(usuarioRepository.findById(user.getUser().getId()).isPresent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAccessDeniedForNonAdmin() throws Exception {
        mockMvc.perform(get("/userSec/getAll"))
                .andExpect(status().isForbidden());
    }
}

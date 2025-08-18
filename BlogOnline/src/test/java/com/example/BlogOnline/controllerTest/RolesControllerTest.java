package com.example.BlogOnline.controllerTest;

import com.example.BlogOnline.DTO.RoleDTO;
import com.example.BlogOnline.Model.Permission;
import com.example.BlogOnline.Model.Role;
import com.example.BlogOnline.Repository.PermissionRepo;
import com.example.BlogOnline.Repository.RoleRepository;
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
public class RolesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepo permissionRepository;

    static {
        MySQLContainer<?> mysql = MySQLTestContainer.getInstance();
        System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysql.getUsername());
        System.setProperty("spring.datasource.password", mysql.getPassword());
    }

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateRole() throws Exception {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName("SUPER_USER");
        roleDTO.setPermissions(new HashSet<>());

        mockMvc.perform(post("/role/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("SUPER_USER")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetRoleById() throws Exception {
        Role role = new Role();
        role.setName("VIEWER");
        Role savedRole = roleRepository.save(role);

        mockMvc.perform(get("/role/GetById/{id}", savedRole.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("VIEWER")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllRoles() throws Exception {
        Role role1 = new Role();
        role1.setName("EDITOR");
        Role role2 = new Role();
        role2.setName("COMMENTER");
        roleRepository.saveAll(Arrays.asList(role1, role2));

        mockMvc.perform(get("/role/GetAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateRole() throws Exception {
        Role role = new Role();
        role.setName("OLD_NAME");
        Role savedRole = roleRepository.save(role);

        RoleDTO updatedDto = new RoleDTO();
        updatedDto.setName("NEW_NAME");
        updatedDto.setPermissions(new HashSet<>());

        mockMvc.perform(put("/role/update/{id}", savedRole.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("NEW_NAME")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteRole() throws Exception {
        Role role = new Role();
        role.setName("TO_DELETE");
        Role savedRole = roleRepository.save(role);

        mockMvc.perform(delete("/role/delete/{id}", savedRole.getId()))
                .andExpect(status().isNoContent());

        assertFalse(roleRepository.findById(savedRole.getId()).isPresent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAccessDeniedForNonAdmin() throws Exception {
        mockMvc.perform(get("/role/GetAll"))
                .andExpect(status().isForbidden());
    }
}
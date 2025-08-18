package com.example.BlogOnline.controllerTest;

import com.example.BlogOnline.DTO.AuthLoginRequestDTO;
import com.example.BlogOnline.DTO.AuthResponseDTO;
import com.example.BlogOnline.Service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        AuthResponseDTO authResponseDTO = new AuthResponseDTO("testuser", "login ok", "test-token", true);
        when(userDetailsService.loginUser(any(AuthLoginRequestDTO.class))).thenReturn(authResponseDTO);
    }

    @Test
    void testLogin() throws Exception {
        AuthLoginRequestDTO authLoginRequestDTO = new AuthLoginRequestDTO("testuser", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authLoginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.message").value("login ok"))
                .andExpect(jsonPath("$.jwt").value("test-token"))
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void testLoginWithInvalidPassword() throws Exception {
        AuthLoginRequestDTO authLoginRequestDTO = new AuthLoginRequestDTO("testuser", "wrongpassword");
        when(userDetailsService.loginUser(any(AuthLoginRequestDTO.class))).thenThrow(new BadCredentialsException("Invalid password"));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authLoginRequestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginWithEmptyPassword() throws Exception {
        AuthLoginRequestDTO authLoginRequestDTO = new AuthLoginRequestDTO("testuser", "");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authLoginRequestDTO)))
                .andExpect(status().isBadRequest());
    }
}

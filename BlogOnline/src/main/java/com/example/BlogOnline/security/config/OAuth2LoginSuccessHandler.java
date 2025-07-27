
package com.example.BlogOnline.security.config;

import com.example.BlogOnline.Model.Role;
import com.example.BlogOnline.Model.UserSec;
import com.example.BlogOnline.Model.Usuario;
import com.example.BlogOnline.Repository.RoleRepository;
import com.example.BlogOnline.Repository.UserSecRepo;
import com.example.BlogOnline.Repository.UsuarioRepository;
import com.example.BlogOnline.utils.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserSecRepo userSecRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) oauth2Token.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = attributes.getOrDefault("email", "").toString();

        // Busca el usuario o lo crea si no existe.
        UserSec user = userSecRepo.findByEmail(email).orElseGet(() -> {
            UserSec newUserSec = new UserSec();
            newUserSec.setEmail(email);
            newUserSec.setUsername(email); // Usar email como username por simplicidad
            newUserSec.setPassword(""); // Sin contraseña para usuarios OAuth2
            newUserSec.setEnabled(true);
            newUserSec.setAccountNonExpired(true);
            newUserSec.setAccountNonLocked(true);
            newUserSec.setCredentialsNonExpired(true);

            Set<Role> roles = new HashSet<>();
            roleRepository.findByName("USER").ifPresent(roles::add);
            newUserSec.setRoles(roles);

            Usuario newUsuario = new Usuario();
            newUsuario.setUsername(email);
            newUsuario.setUserSec(newUserSec);
            newUsuario.setEnabled(true);

            usuarioRepository.save(newUsuario);
            return newUserSec;
        });

        // Carga el usuario desde la base de datos para obtener sus roles y permisos reales.
        UserSec userFromDb = userSecRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Error: El usuario no fue encontrado en la base de datos después del proceso de creación/búsqueda."));

        // Crea la lista de autoridades (roles y permisos) desde la base de datos.
        Set<SimpleGrantedAuthority> authorities = userFromDb.getRoles().stream()
                .flatMap(role -> {
                    Set<SimpleGrantedAuthority> auths = new HashSet<>();
                    // Agrega el rol (ej. "ROLE_USER")
                    auths.add(new SimpleGrantedAuthority(role.getName()));
                    // Agrega los permisos asociados al rol (ej. "READ", "WRITE")
                    if (role.getPermissions() != null) {
                        role.getPermissions().stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                                .forEach(auths::add);
                    }
                    return auths.stream();
                })
                .collect(Collectors.toSet());

        // Crea el objeto Authentication con las autoridades correctas de la base de datos.
        Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                email, null, authorities);

        // Crea el JWT con los roles y permisos correctos y redirige al frontend.
        String jwt = jwtUtils.createToken(auth);
        response.sendRedirect("/?token=" + jwt);
    }
}


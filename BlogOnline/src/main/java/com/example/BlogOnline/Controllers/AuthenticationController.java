package com.example.BlogOnline.Controllers;


import com.example.BlogOnline.DTO.AuthLoginRequestDTO;
import com.example.BlogOnline.Service.UserDetailsServiceImpl;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class AuthenticationController {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    //Todas estas requests y responses vamos a tratarlas como dto
    @PostMapping("/auth/login")
    public ResponseEntity login(@RequestBody @Valid AuthLoginRequestDTO userRequest) {

       if (userRequest.password() == null || userRequest.password().isEmpty()) {
            // El password está vacío o es null, es necesario el siguiento código, si no no genera el token si no hay Password
           return new ResponseEntity<>(this.userDetailsService.loginOAuthUser(userRequest), HttpStatus.OK);
        }
        return new ResponseEntity<>(this.userDetailsService.loginUser(userRequest), HttpStatus.OK);
    }

}



package com.example.BlogOnline.DTO;

import lombok.Data;
import java.util.Set;

@Data
public class UsuarioDto {
    private Long id;
    private String username;
    private Set<Long> posteoIds;
    private boolean enabled = true;

}
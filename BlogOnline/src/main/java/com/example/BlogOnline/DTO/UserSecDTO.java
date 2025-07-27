package com.example.BlogOnline.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSecDTO {



    @NotNull(message = "id can not to be null")
    private Long id;

    @NotBlank(message = "name can't to be null")
    private String username;

    private String email;


    private String password;
    private Boolean enabled = true;
    private Boolean accountNonExpired= true;
    private Boolean accountNonLocked = true;
    private Boolean credentialsNonExpired = true;


    private Set<RoleDTO> roles;
}

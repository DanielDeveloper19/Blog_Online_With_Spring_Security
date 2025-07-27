package com.example.BlogOnline.DTO;


import com.example.BlogOnline.Model.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosteoDTO {

    private Long id;

    @NotBlank()
    private String content;

    private String userName;

    private boolean enabled = true;

}

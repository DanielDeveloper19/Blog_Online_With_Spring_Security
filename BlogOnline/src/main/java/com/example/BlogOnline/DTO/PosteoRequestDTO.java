package com.example.BlogOnline.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PosteoRequestDTO {

@NotBlank(message = "Debes escribir algo en el mensaje")
    private String content;

    private Boolean enabled=true;

}

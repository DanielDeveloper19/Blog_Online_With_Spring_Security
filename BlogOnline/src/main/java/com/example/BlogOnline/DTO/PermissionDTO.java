package com.example.BlogOnline.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.IMessage;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTO {

  @NotNull(message = "El id no puede ser nulo o vacío")
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
}

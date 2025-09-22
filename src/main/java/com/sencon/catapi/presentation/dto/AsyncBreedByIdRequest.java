package com.sencon.catapi.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AsyncBreedByIdRequest(
    @NotBlank(message = "Email é obrigatório") 
    @Email(message = "Email deve ter um formato válido") 
    String email,
    
    @NotNull(message = "ID da raça é obrigatório")
    UUID breedId,
    
    boolean includeImages
) {}

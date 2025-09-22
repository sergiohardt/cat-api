package com.sencon.catapi.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AsyncBreedRequest(
    @NotBlank(message = "Email é obrigatório") 
    @Email(message = "Email deve ter um formato válido") 
    String email,
    
    boolean includeImages
) {}

package com.sencon.catapi.domain.enums;

public enum ImageType {
    BREED("Imagem de raça específica"),
    HAT("Imagem de gato com chapéu"),
    SUNGLASSES("Imagem de gato com óculos");

    private final String description;

    ImageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

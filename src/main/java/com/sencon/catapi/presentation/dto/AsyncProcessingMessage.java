package com.sencon.catapi.presentation.dto;

import java.util.UUID;

public record AsyncProcessingMessage(
    String requestId,
    String requestType,
    String email,
    String queryParameters,
    Long timestamp
) {
    
    public static AsyncProcessingMessage getAllBreeds(String email, boolean includeImages) {
        return new AsyncProcessingMessage(
            UUID.randomUUID().toString(),
            "GET_ALL_BREEDS",
            email,
            String.format("includeImages=%s", includeImages),
            System.currentTimeMillis()
        );
    }
    
    public static AsyncProcessingMessage getBreedById(String email, UUID breedId, boolean includeImages) {
        return new AsyncProcessingMessage(
            UUID.randomUUID().toString(),
            "GET_BREED_BY_ID",
            email,
            String.format("breedId=%s&includeImages=%s", breedId, includeImages),
            System.currentTimeMillis()
        );
    }
    
    public static AsyncProcessingMessage getBreedsByTemperament(String email, String temperament, boolean includeImages) {
        return new AsyncProcessingMessage(
            UUID.randomUUID().toString(),
            "GET_BREEDS_BY_TEMPERAMENT",
            email,
            String.format("temperament=%s&includeImages=%s", temperament, includeImages),
            System.currentTimeMillis()
        );
    }
    
    public static AsyncProcessingMessage getBreedsByOrigin(String email, String origin, boolean includeImages) {
        return new AsyncProcessingMessage(
            UUID.randomUUID().toString(),
            "GET_BREEDS_BY_ORIGIN",
            email,
            String.format("origin=%s&includeImages=%s", origin, includeImages),
            System.currentTimeMillis()
        );
    }
}

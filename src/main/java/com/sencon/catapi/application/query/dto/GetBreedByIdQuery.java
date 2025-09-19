package com.sencon.catapi.application.query.dto;

import java.util.UUID;

public class GetBreedByIdQuery {
    
    private UUID breedId;
    private boolean includeImages;
    
    public GetBreedByIdQuery() {
    }
    
    public GetBreedByIdQuery(UUID breedId, boolean includeImages) {
        this.breedId = breedId;
        this.includeImages = includeImages;
    }
    
    public UUID getBreedId() {
        return breedId;
    }
    
    public void setBreedId(UUID breedId) {
        this.breedId = breedId;
    }
    
    public boolean isIncludeImages() {
        return includeImages;
    }
    
    public void setIncludeImages(boolean includeImages) {
        this.includeImages = includeImages;
    }
}

package com.sencon.catapi.application.query.dto;

public class GetBreedsByOriginQuery {
    
    private String origin;
    private boolean includeImages;
    
    public GetBreedsByOriginQuery() {
    }
    
    public GetBreedsByOriginQuery(String origin, boolean includeImages) {
        this.origin = origin;
        this.includeImages = includeImages;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public boolean isIncludeImages() {
        return includeImages;
    }
    
    public void setIncludeImages(boolean includeImages) {
        this.includeImages = includeImages;
    }
}

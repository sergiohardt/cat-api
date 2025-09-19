package com.sencon.catapi.application.query.dto;

public class GetBreedsByTemperamentQuery {
    
    private String temperament;
    private boolean includeImages;
    
    public GetBreedsByTemperamentQuery() {
    }
    
    public GetBreedsByTemperamentQuery(String temperament, boolean includeImages) {
        this.temperament = temperament;
        this.includeImages = includeImages;
    }
    
    public String getTemperament() {
        return temperament;
    }
    
    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }
    
    public boolean isIncludeImages() {
        return includeImages;
    }
    
    public void setIncludeImages(boolean includeImages) {
        this.includeImages = includeImages;
    }
}

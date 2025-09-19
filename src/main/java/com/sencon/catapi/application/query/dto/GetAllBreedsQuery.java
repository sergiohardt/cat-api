package com.sencon.catapi.application.query.dto;

public class GetAllBreedsQuery {
    
    private boolean includeImages;
    private String sortBy;
    private String sortDirection;
    
    public GetAllBreedsQuery() {
        this.includeImages = false;
        this.sortBy = "name";
        this.sortDirection = "ASC";
    }
    
    public GetAllBreedsQuery(boolean includeImages, String sortBy, String sortDirection) {
        this.includeImages = includeImages;
        this.sortBy = sortBy != null ? sortBy : "name";
        this.sortDirection = sortDirection != null ? sortDirection : "ASC";
    }
    
    public boolean isIncludeImages() {
        return includeImages;
    }
    
    public void setIncludeImages(boolean includeImages) {
        this.includeImages = includeImages;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}

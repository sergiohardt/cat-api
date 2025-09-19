package com.sencon.catapi.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class CollectionRequest {
    
    private boolean forceUpdate = false;
    private boolean collectBreedImages = true;
    private boolean collectHatImages = true;
    private boolean collectSunglassesImages = true;
    
    @Min(value = 1, message = "Número de imagens por raça deve ser pelo menos 1")
    @Max(value = 10, message = "Número de imagens por raça não pode ser maior que 10")
    private int imagesPerBreed = 3;
    
    @Min(value = 1, message = "Número de imagens especiais deve ser pelo menos 1")
    @Max(value = 20, message = "Número de imagens especiais não pode ser maior que 20")
    private int specialImagesCount = 3;
    
    public CollectionRequest() {
    }
    
    public boolean isForceUpdate() {
        return forceUpdate;
    }
    
    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
    
    public boolean isCollectBreedImages() {
        return collectBreedImages;
    }
    
    public void setCollectBreedImages(boolean collectBreedImages) {
        this.collectBreedImages = collectBreedImages;
    }
    
    public boolean isCollectHatImages() {
        return collectHatImages;
    }
    
    public void setCollectHatImages(boolean collectHatImages) {
        this.collectHatImages = collectHatImages;
    }
    
    public boolean isCollectSunglassesImages() {
        return collectSunglassesImages;
    }
    
    public void setCollectSunglassesImages(boolean collectSunglassesImages) {
        this.collectSunglassesImages = collectSunglassesImages;
    }
    
    public int getImagesPerBreed() {
        return imagesPerBreed;
    }
    
    public void setImagesPerBreed(int imagesPerBreed) {
        this.imagesPerBreed = imagesPerBreed;
    }
    
    public int getSpecialImagesCount() {
        return specialImagesCount;
    }
    
    public void setSpecialImagesCount(int specialImagesCount) {
        this.specialImagesCount = specialImagesCount;
    }
}

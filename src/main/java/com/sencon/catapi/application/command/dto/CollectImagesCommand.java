package com.sencon.catapi.application.command.dto;

public class CollectImagesCommand {
    
    private boolean collectBreedImages;
    private boolean collectHatImages;
    private boolean collectSunglassesImages;
    private int imagesPerBreed;
    private int specialImagesCount;
    
    public CollectImagesCommand() {
        this.collectBreedImages = true;
        this.collectHatImages = true;
        this.collectSunglassesImages = true;
        this.imagesPerBreed = 3;
        this.specialImagesCount = 3;
    }
    
    public CollectImagesCommand(boolean collectBreedImages, boolean collectHatImages, 
                              boolean collectSunglassesImages, int imagesPerBreed, int specialImagesCount) {
        this.collectBreedImages = collectBreedImages;
        this.collectHatImages = collectHatImages;
        this.collectSunglassesImages = collectSunglassesImages;
        this.imagesPerBreed = imagesPerBreed;
        this.specialImagesCount = specialImagesCount;
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

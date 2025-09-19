package com.sencon.catapi.domain.model;

import com.sencon.catapi.domain.enums.ImageType;

import java.time.LocalDateTime;
import java.util.UUID;

public class CatImage {
    
    private UUID id;
    private String externalId;
    private String url;
    private Integer width;
    private Integer height;
    private ImageType imageType;
    private UUID breedId;
    private LocalDateTime createdAt;

    public CatImage() {
    }

    public CatImage(String externalId, String url, Integer width, Integer height, ImageType imageType) {
        this.externalId = externalId;
        this.url = url;
        this.width = width;
        this.height = height;
        this.imageType = imageType;
        this.createdAt = LocalDateTime.now();
    }

    public CatImage(String externalId, String url, Integer width, Integer height, ImageType imageType, UUID breedId) {
        this(externalId, url, width, height, imageType);
        this.breedId = breedId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public UUID getBreedId() {
        return breedId;
    }

    public void setBreedId(UUID breedId) {
        this.breedId = breedId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

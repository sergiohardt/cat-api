package com.sencon.catapi.infrastructure.persistence.entity;

import com.sencon.catapi.domain.enums.ImageType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cat_image")
public class CatImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "external_id", unique = true, nullable = false)
    private String externalId;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    private ImageType imageType;

    @Column(name = "breed_id")
    private UUID breedId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public CatImageEntity() {
    }

    public CatImageEntity(String externalId, String url, Integer width, Integer height, ImageType imageType) {
        this.externalId = externalId;
        this.url = url;
        this.width = width;
        this.height = height;
        this.imageType = imageType;
    }

    public CatImageEntity(String externalId, String url, Integer width, Integer height, ImageType imageType, UUID breedId) {
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

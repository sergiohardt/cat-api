package com.sencon.catapi.infrastructure.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CatApiImageDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("url")
    private String url;

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("breeds")
    private List<CatApiBreedDto> breeds;

    @JsonProperty("categories")
    private List<CatApiCategoryDto> categories;

    public CatApiImageDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<CatApiBreedDto> getBreeds() {
        return breeds;
    }

    public void setBreeds(List<CatApiBreedDto> breeds) {
        this.breeds = breeds;
    }

    public List<CatApiCategoryDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CatApiCategoryDto> categories) {
        this.categories = categories;
    }
}

package com.sencon.catapi.infrastructure.persistence.mapper;

import com.sencon.catapi.domain.model.CatImage;
import com.sencon.catapi.infrastructure.persistence.entity.CatImageEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CatImageEntityMapper {

    public CatImageEntity toEntity(CatImage domain) {
        if (domain == null) {
            return null;
        }

        CatImageEntity entity = new CatImageEntity();
        entity.setId(domain.getId());
        entity.setExternalId(domain.getExternalId());
        entity.setUrl(domain.getUrl());
        entity.setWidth(domain.getWidth());
        entity.setHeight(domain.getHeight());
        entity.setImageType(domain.getImageType());
        entity.setBreedId(domain.getBreedId());
        entity.setCreatedAt(domain.getCreatedAt());

        return entity;
    }

    public CatImage toDomain(CatImageEntity entity) {
        if (entity == null) {
            return null;
        }

        CatImage domain = new CatImage();
        domain.setId(entity.getId());
        domain.setExternalId(entity.getExternalId());
        domain.setUrl(entity.getUrl());
        domain.setWidth(entity.getWidth());
        domain.setHeight(entity.getHeight());
        domain.setImageType(entity.getImageType());
        domain.setBreedId(entity.getBreedId());
        domain.setCreatedAt(entity.getCreatedAt());

        return domain;
    }

    public List<CatImage> toDomainList(List<CatImageEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<CatImageEntity> toEntityList(List<CatImage> domains) {
        if (domains == null) {
            return null;
        }

        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}

package com.sencon.catapi.infrastructure.persistence.mapper;

import com.sencon.catapi.domain.model.CatBreed;
import com.sencon.catapi.infrastructure.persistence.entity.CatBreedEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CatBreedEntityMapper {

    public CatBreedEntity toEntity(CatBreed domain) {
        if (domain == null) {
            return null;
        }

        CatBreedEntity entity = new CatBreedEntity();
        entity.setId(domain.getId());
        entity.setExternalId(domain.getExternalId());
        entity.setName(domain.getName());
        entity.setOrigin(domain.getOrigin());
        entity.setTemperament(domain.getTemperament());
        entity.setDescription(domain.getDescription());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }

    public CatBreed toDomain(CatBreedEntity entity) {
        if (entity == null) {
            return null;
        }

        CatBreed domain = new CatBreed();
        domain.setId(entity.getId());
        domain.setExternalId(entity.getExternalId());
        domain.setName(entity.getName());
        domain.setOrigin(entity.getOrigin());
        domain.setTemperament(entity.getTemperament());
        domain.setDescription(entity.getDescription());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());

        return domain;
    }

    public List<CatBreed> toDomainList(List<CatBreedEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<CatBreedEntity> toEntityList(List<CatBreed> domains) {
        if (domains == null) {
            return null;
        }

        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}

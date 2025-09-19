package com.sencon.catapi.infrastructure.external.mapper;

import com.sencon.catapi.domain.enums.ImageType;
import com.sencon.catapi.domain.model.CatBreed;
import com.sencon.catapi.domain.model.CatImage;
import com.sencon.catapi.infrastructure.external.dto.CatApiBreedDto;
import com.sencon.catapi.infrastructure.external.dto.CatApiImageDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CatApiMapper {

    public CatBreed toDomain(CatApiBreedDto dto) {
        if (dto == null) {
            return null;
        }

        return new CatBreed(
                dto.getId(),
                dto.getName(),
                dto.getOrigin(),
                dto.getTemperament(),
                dto.getDescription()
        );
    }

    public CatImage toDomain(CatApiImageDto dto, ImageType imageType) {
        if (dto == null) {
            return null;
        }

        return new CatImage(
                dto.getId(),
                dto.getUrl(),
                dto.getWidth(),
                dto.getHeight(),
                imageType
        );
    }

    public CatImage toDomain(CatApiImageDto dto, ImageType imageType, UUID breedId) {
        if (dto == null) {
            return null;
        }

        return new CatImage(
                dto.getId(),
                dto.getUrl(),
                dto.getWidth(),
                dto.getHeight(),
                imageType,
                breedId
        );
    }
}

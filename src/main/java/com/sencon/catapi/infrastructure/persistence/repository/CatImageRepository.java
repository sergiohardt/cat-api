package com.sencon.catapi.infrastructure.persistence.repository;

import com.sencon.catapi.domain.enums.ImageType;
import com.sencon.catapi.infrastructure.persistence.entity.CatImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CatImageRepository extends JpaRepository<CatImageEntity, UUID> {

    Optional<CatImageEntity> findByExternalId(String externalId);

    List<CatImageEntity> findByImageType(ImageType imageType);

    List<CatImageEntity> findByBreedId(UUID breedId);

    @Query("SELECT ci FROM CatImageEntity ci WHERE ci.imageType = :imageType AND ci.breedId = :breedId")
    List<CatImageEntity> findByImageTypeAndBreedId(@Param("imageType") ImageType imageType, @Param("breedId") UUID breedId);

    @Query("SELECT COUNT(ci) FROM CatImageEntity ci WHERE ci.imageType = :imageType")
    long countByImageType(@Param("imageType") ImageType imageType);

    @Query("SELECT COUNT(ci) FROM CatImageEntity ci WHERE ci.breedId = :breedId")
    long countByBreedId(@Param("breedId") UUID breedId);

    boolean existsByExternalId(String externalId);

    @Query("SELECT ci FROM CatImageEntity ci WHERE ci.imageType = :imageType ORDER BY ci.createdAt DESC")
    List<CatImageEntity> findByImageTypeOrderByCreatedAtDesc(@Param("imageType") ImageType imageType);
}

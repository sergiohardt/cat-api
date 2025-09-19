package com.sencon.catapi.infrastructure.persistence.repository;

import com.sencon.catapi.infrastructure.persistence.entity.CatBreedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CatBreedRepository extends JpaRepository<CatBreedEntity, UUID> {

    Optional<CatBreedEntity> findByExternalId(String externalId);

    @Query("SELECT cb FROM CatBreedEntity cb WHERE LOWER(cb.temperament) LIKE LOWER(CONCAT('%', :temperament, '%'))")
    List<CatBreedEntity> findByTemperamentContainingIgnoreCase(@Param("temperament") String temperament);

    @Query("SELECT cb FROM CatBreedEntity cb WHERE LOWER(cb.origin) LIKE LOWER(CONCAT('%', :origin, '%'))")
    List<CatBreedEntity> findByOriginContainingIgnoreCase(@Param("origin") String origin);

    @Query("SELECT DISTINCT cb.origin FROM CatBreedEntity cb WHERE cb.origin IS NOT NULL ORDER BY cb.origin")
    List<String> findAllOrigins();

    @Query("SELECT cb FROM CatBreedEntity cb ORDER BY cb.name")
    List<CatBreedEntity> findAllOrderByName();

    boolean existsByExternalId(String externalId);
}

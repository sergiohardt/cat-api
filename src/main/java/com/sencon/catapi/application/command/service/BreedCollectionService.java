package com.sencon.catapi.application.command.service;

import com.sencon.catapi.domain.model.CatBreed;
import com.sencon.catapi.infrastructure.external.client.CatApiClient;
import com.sencon.catapi.infrastructure.external.mapper.CatApiMapper;
import com.sencon.catapi.infrastructure.persistence.entity.CatBreedEntity;
import com.sencon.catapi.infrastructure.persistence.mapper.CatBreedEntityMapper;
import com.sencon.catapi.infrastructure.persistence.repository.CatBreedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BreedCollectionService {

    private static final Logger logger = LoggerFactory.getLogger(BreedCollectionService.class);

    private final CatApiClient catApiClient;
    private final CatApiMapper catApiMapper;
    private final CatBreedRepository breedRepository;
    private final CatBreedEntityMapper breedEntityMapper;

    public BreedCollectionService(CatApiClient catApiClient,
                                CatApiMapper catApiMapper,
                                CatBreedRepository breedRepository,
                                CatBreedEntityMapper breedEntityMapper) {
        this.catApiClient = catApiClient;
        this.catApiMapper = catApiMapper;
        this.breedRepository = breedRepository;
        this.breedEntityMapper = breedEntityMapper;
    }

    @Transactional
    public CompletableFuture<Integer> collectBreeds(boolean forceUpdate) {
        logger.info("Iniciando coleta de raças. Force update: {}", forceUpdate);

        return catApiClient.getAllBreeds()
                .map(catApiMapper::toDomain)
                .collectList()
                .map(breeds -> saveBreeds(breeds, forceUpdate))
                .toFuture();
    }

    private Integer saveBreeds(List<CatBreed> breeds, boolean forceUpdate) {
        AtomicInteger savedCount = new AtomicInteger(0);
        AtomicInteger updatedCount = new AtomicInteger(0);

        breeds.parallelStream().forEach(breed -> {
            try {
                if (breedRepository.existsByExternalId(breed.getExternalId())) {
                    if (forceUpdate) {
                        updateExistingBreed(breed);
                        updatedCount.incrementAndGet();
                        logger.debug("Raça atualizada: {}", breed.getName());
                    } else {
                        logger.debug("Raça já existe, pulando: {}", breed.getName());
                    }
                } else {
                    saveNewBreed(breed);
                    savedCount.incrementAndGet();
                    logger.debug("Nova raça salva: {}", breed.getName());
                }
            } catch (Exception e) {
                logger.error("Erro ao processar raça {}: ", breed.getName(), e);
            }
        });

        int totalProcessed = savedCount.get() + updatedCount.get();
        logger.info("Coleta de raças concluída. Novas: {}, Atualizadas: {}, Total: {}", 
                   savedCount.get(), updatedCount.get(), totalProcessed);

        return totalProcessed;
    }

    private void saveNewBreed(CatBreed breed) {
        CatBreedEntity entity = breedEntityMapper.toEntity(breed);
        breedRepository.save(entity);
    }

    private void updateExistingBreed(CatBreed breed) {
        breedRepository.findByExternalId(breed.getExternalId())
                .ifPresent(existingEntity -> {
                    existingEntity.setName(breed.getName());
                    existingEntity.setOrigin(breed.getOrigin());
                    existingEntity.setTemperament(breed.getTemperament());
                    existingEntity.setDescription(breed.getDescription());
                    breedRepository.save(existingEntity);
                });
    }
}

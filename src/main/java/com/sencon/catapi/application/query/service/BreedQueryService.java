package com.sencon.catapi.application.query.service;

import com.sencon.catapi.domain.model.CatBreed;
import com.sencon.catapi.domain.model.CatImage;
import com.sencon.catapi.infrastructure.persistence.entity.CatBreedEntity;
import com.sencon.catapi.infrastructure.persistence.entity.CatImageEntity;
import com.sencon.catapi.infrastructure.persistence.mapper.CatBreedEntityMapper;
import com.sencon.catapi.infrastructure.persistence.mapper.CatImageEntityMapper;
import com.sencon.catapi.infrastructure.persistence.repository.CatBreedRepository;
import com.sencon.catapi.infrastructure.persistence.repository.CatImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
public class BreedQueryService {

    private static final Logger logger = LoggerFactory.getLogger(BreedQueryService.class);

    private final CatBreedRepository breedRepository;
    private final CatImageRepository imageRepository;
    private final CatBreedEntityMapper breedEntityMapper;
    private final CatImageEntityMapper imageEntityMapper;

    public BreedQueryService(CatBreedRepository breedRepository,
                           CatImageRepository imageRepository,
                           CatBreedEntityMapper breedEntityMapper,
                           CatImageEntityMapper imageEntityMapper) {
        this.breedRepository = breedRepository;
        this.imageRepository = imageRepository;
        this.breedEntityMapper = breedEntityMapper;
        this.imageEntityMapper = imageEntityMapper;
    }

    @Cacheable(value = "breeds", key = "'all-breeds'")
    public List<CatBreed> getAllBreeds() {
        logger.debug("Buscando todas as raças");
        List<CatBreedEntity> entities = breedRepository.findAllOrderByName();
        return breedEntityMapper.toDomainList(entities);
    }

    @Async
    @Cacheable(value = "breeds", key = "'all-breeds-with-images'")
    public CompletableFuture<List<BreedWithImages>> getAllBreedsWithImages() {
        logger.debug("Buscando todas as raças com imagens de forma assíncrona");
        
        return CompletableFuture.supplyAsync(() -> {
            List<CatBreedEntity> breedEntities = breedRepository.findAllOrderByName();
            
            return breedEntities.parallelStream()
                    .map(this::mapToBreedWithImages)
                    .toList();
        });
    }

    @Cacheable(value = "breeds", key = "#breedId")
    public Optional<CatBreed> getBreedById(UUID breedId) {
        logger.debug("Buscando raça por ID: {}", breedId);
        return breedRepository.findById(breedId)
                .map(breedEntityMapper::toDomain);
    }

    @Async
    @Cacheable(value = "breeds", key = "'breed-with-images-' + #breedId")
    public CompletableFuture<Optional<BreedWithImages>> getBreedWithImagesById(UUID breedId) {
        logger.debug("Buscando raça com imagens por ID de forma assíncrona: {}", breedId);
        
        return CompletableFuture.supplyAsync(() -> 
            breedRepository.findById(breedId)
                    .map(this::mapToBreedWithImages)
        );
    }

    @Async
    @Cacheable(value = "queries", key = "'temperament-' + #temperament")
    public CompletableFuture<List<CatBreed>> getBreedsByTemperament(String temperament) {
        logger.debug("Buscando raças por temperamento de forma assíncrona: {}", temperament);
        
        return CompletableFuture.supplyAsync(() -> {
            List<CatBreedEntity> entities = breedRepository.findByTemperamentContainingIgnoreCase(temperament);
            return breedEntityMapper.toDomainList(entities);
        });
    }

    @Async
    @Cacheable(value = "queries", key = "'temperament-with-images-' + #temperament")
    public CompletableFuture<List<BreedWithImages>> getBreedsByTemperamentWithImages(String temperament) {
        logger.debug("Buscando raças com imagens por temperamento de forma assíncrona: {}", temperament);
        
        return CompletableFuture.supplyAsync(() -> {
            List<CatBreedEntity> breedEntities = breedRepository.findByTemperamentContainingIgnoreCase(temperament);
            
            return breedEntities.parallelStream()
                    .map(this::mapToBreedWithImages)
                    .toList();
        });
    }

    @Async
    @Cacheable(value = "queries", key = "'origin-' + #origin")
    public CompletableFuture<List<CatBreed>> getBreedsByOrigin(String origin) {
        logger.debug("Buscando raças por origem de forma assíncrona: {}", origin);
        
        return CompletableFuture.supplyAsync(() -> {
            List<CatBreedEntity> entities = breedRepository.findByOriginContainingIgnoreCase(origin);
            return breedEntityMapper.toDomainList(entities);
        });
    }

    @Async
    @Cacheable(value = "queries", key = "'origin-with-images-' + #origin")
    public CompletableFuture<List<BreedWithImages>> getBreedsByOriginWithImages(String origin) {
        logger.debug("Buscando raças com imagens por origem de forma assíncrona: {}", origin);
        
        return CompletableFuture.supplyAsync(() -> {
            List<CatBreedEntity> breedEntities = breedRepository.findByOriginContainingIgnoreCase(origin);
            
            return breedEntities.parallelStream()
                    .map(this::mapToBreedWithImages)
                    .toList();
        });
    }

    private BreedWithImages mapToBreedWithImages(CatBreedEntity breedEntity) {
        CatBreed breed = breedEntityMapper.toDomain(breedEntity);
        List<CatImageEntity> imageEntities = imageRepository.findByBreedId(breedEntity.getId());
        List<CatImage> images = imageEntityMapper.toDomainList(imageEntities);
        
        return new BreedWithImages(breed, images);
    }

    public static class BreedWithImages {
        private final CatBreed breed;
        private final List<CatImage> images;

        public BreedWithImages(CatBreed breed, List<CatImage> images) {
            this.breed = breed;
            this.images = images;
        }

        public CatBreed getBreed() {
            return breed;
        }

        public List<CatImage> getImages() {
            return images;
        }
    }
}

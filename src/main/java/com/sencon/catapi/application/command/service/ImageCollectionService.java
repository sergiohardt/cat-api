package com.sencon.catapi.application.command.service;

import com.sencon.catapi.domain.enums.ImageType;
import com.sencon.catapi.domain.model.CatImage;
import com.sencon.catapi.infrastructure.external.client.CatApiClient;
import com.sencon.catapi.infrastructure.external.dto.CatApiImageDto;
import com.sencon.catapi.infrastructure.external.mapper.CatApiMapper;
import com.sencon.catapi.infrastructure.persistence.entity.CatBreedEntity;
import com.sencon.catapi.infrastructure.persistence.entity.CatImageEntity;
import com.sencon.catapi.infrastructure.persistence.mapper.CatImageEntityMapper;
import com.sencon.catapi.infrastructure.persistence.repository.CatBreedRepository;
import com.sencon.catapi.infrastructure.persistence.repository.CatImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ImageCollectionService {

    private static final Logger logger = LoggerFactory.getLogger(ImageCollectionService.class);

    private final CatApiClient catApiClient;
    private final CatApiMapper catApiMapper;
    private final CatImageRepository imageRepository;
    private final CatBreedRepository breedRepository;
    private final CatImageEntityMapper imageEntityMapper;

    public ImageCollectionService(CatApiClient catApiClient,
                                CatApiMapper catApiMapper,
                                CatImageRepository imageRepository,
                                CatBreedRepository breedRepository,
                                CatImageEntityMapper imageEntityMapper) {
        this.catApiClient = catApiClient;
        this.catApiMapper = catApiMapper;
        this.imageRepository = imageRepository;
        this.breedRepository = breedRepository;
        this.imageEntityMapper = imageEntityMapper;
    }

    @Async
    @Transactional
    public CompletableFuture<Integer> collectBreedImages(int imagesPerBreed) {
        logger.info("Iniciando coleta de imagens por raça. Limite: {} por raça", imagesPerBreed);

        List<CatBreedEntity> breeds = breedRepository.findAll();
        AtomicInteger totalImages = new AtomicInteger(0);

        List<CompletableFuture<Integer>> futures = breeds.stream()
                .map(breed -> collectImagesForBreed(breed, imagesPerBreed))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    int total = futures.stream()
                            .mapToInt(future -> future.join())
                            .sum();
                    logger.info("Coleta de imagens por raça concluída. Total: {} imagens", total);
                    return total;
                });
    }

    @Async
    @Transactional
    public CompletableFuture<Integer> collectHatImages(int limit) {
        logger.info("Iniciando coleta de imagens com chapéu. Limite: {}", limit);

        return catApiClient.getImagesWithHats(limit)
                .map(images -> saveSpecialImages(images, ImageType.HAT))
                .toFuture();
    }

    @Async
    @Transactional
    public CompletableFuture<Integer> collectSunglassesImages(int limit) {
        logger.info("Iniciando coleta de imagens com óculos. Limite: {}", limit);

        return catApiClient.getImagesWithSunglasses(limit)
                .map(images -> saveSpecialImages(images, ImageType.SUNGLASSES))
                .toFuture();
    }

    private CompletableFuture<Integer> collectImagesForBreed(CatBreedEntity breed, int limit) {
        return catApiClient.getImagesByBreed(breed.getExternalId(), limit)
                .collectList()
                .map(images -> saveBreedImages(images, breed, limit))
                .toFuture();
    }

    private Integer saveBreedImages(List<CatApiImageDto> imageDtos, CatBreedEntity breed, int limit) {
        AtomicInteger savedCount = new AtomicInteger(0);

        imageDtos.stream()
                .limit(limit)
                .forEach(imageDto -> {
                    try {
                        if (!imageRepository.existsByExternalId(imageDto.getId())) {
                            CatImage catImage = catApiMapper.toDomain(imageDto, ImageType.BREED, breed.getId());
                            CatImageEntity entity = imageEntityMapper.toEntity(catImage);
                            imageRepository.save(entity);
                            savedCount.incrementAndGet();
                            logger.debug("Imagem salva para raça {}: {}", breed.getName(), imageDto.getUrl());
                        }
                    } catch (Exception e) {
                        logger.error("Erro ao salvar imagem {} para raça {}: ", imageDto.getId(), breed.getName(), e);
                    }
                });

        logger.debug("Salvas {} imagens para a raça: {}", savedCount.get(), breed.getName());
        return savedCount.get();
    }

    private Integer saveSpecialImages(List<CatApiImageDto> imageDtos, ImageType imageType) {
        AtomicInteger savedCount = new AtomicInteger(0);

        imageDtos.parallelStream()
                .forEach(imageDto -> {
                    try {
                        if (!imageRepository.existsByExternalId(imageDto.getId())) {
                            CatImage catImage = catApiMapper.toDomain(imageDto, imageType);
                            CatImageEntity entity = imageEntityMapper.toEntity(catImage);
                            imageRepository.save(entity);
                            savedCount.incrementAndGet();
                            logger.debug("Imagem especial salva ({}): {}", imageType, imageDto.getUrl());
                        }
                    } catch (Exception e) {
                        logger.error("Erro ao salvar imagem especial {} ({}): ", imageDto.getId(), imageType, e);
                    }
                });

        logger.info("Salvas {} imagens do tipo: {}", savedCount.get(), imageType);
        return savedCount.get();
    }
}

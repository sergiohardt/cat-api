package com.sencon.catapi.application.command.handler;

import com.sencon.catapi.application.command.dto.CollectImagesCommand;
import com.sencon.catapi.application.command.service.ImageCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class CollectImagesCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(CollectImagesCommandHandler.class);

    private final ImageCollectionService imageCollectionService;

    public CollectImagesCommandHandler(ImageCollectionService imageCollectionService) {
        this.imageCollectionService = imageCollectionService;
    }

    public CompletableFuture<CollectionResult> handle(CollectImagesCommand command) {
        logger.info("Processando comando de coleta de imagens");

        List<CompletableFuture<Integer>> futures = new ArrayList<>();

        if (command.isCollectBreedImages()) {
            futures.add(imageCollectionService.collectBreedImages(command.getImagesPerBreed()));
        }

        if (command.isCollectHatImages()) {
            futures.add(imageCollectionService.collectHatImages(command.getSpecialImagesCount()));
        }

        if (command.isCollectSunglassesImages()) {
            futures.add(imageCollectionService.collectSunglassesImages(command.getSpecialImagesCount()));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    int totalImages = futures.stream()
                            .mapToInt(future -> future.join())
                            .sum();
                    
                    CollectionResult result = new CollectionResult(totalImages, futures.size());
                    logger.info("Comando de coleta de imagens processado com sucesso. Total: {} imagens", totalImages);
                    return result;
                })
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Erro ao processar comando de coleta de imagens: ", throwable);
                    }
                });
    }

    public static class CollectionResult {
        private final int totalImages;
        private final int collectionsExecuted;

        public CollectionResult(int totalImages, int collectionsExecuted) {
            this.totalImages = totalImages;
            this.collectionsExecuted = collectionsExecuted;
        }

        public int getTotalImages() {
            return totalImages;
        }

        public int getCollectionsExecuted() {
            return collectionsExecuted;
        }
    }
}

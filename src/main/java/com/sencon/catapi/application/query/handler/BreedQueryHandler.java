package com.sencon.catapi.application.query.handler;

import com.sencon.catapi.application.query.dto.*;
import com.sencon.catapi.application.query.service.BreedQueryService;
import com.sencon.catapi.domain.model.CatBreed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class BreedQueryHandler {

    private static final Logger logger = LoggerFactory.getLogger(BreedQueryHandler.class);

    private final BreedQueryService breedQueryService;

    public BreedQueryHandler(BreedQueryService breedQueryService) {
        this.breedQueryService = breedQueryService;
    }

    public CompletableFuture<Object> handle(GetAllBreedsQuery query) {
        logger.debug("Processando consulta de todas as raças. Include images: {}", query.isIncludeImages());

        if (query.isIncludeImages()) {
            return breedQueryService.getAllBreedsWithImages()
                    .thenApply(breeds -> (Object) breeds)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.error("Erro ao processar consulta de todas as raças com imagens: ", throwable);
                        } else {
                            @SuppressWarnings("unchecked")
                            List<?> list = (List<?>) result;
                            logger.debug("Consulta de todas as raças com imagens processada com sucesso. Total: {}", list.size());
                        }
                    });
        } else {
            return CompletableFuture.completedFuture((Object) breedQueryService.getAllBreeds())
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.error("Erro ao processar consulta de todas as raças: ", throwable);
                        } else {
                            @SuppressWarnings("unchecked")
                            List<?> list = (List<?>) result;
                            logger.debug("Consulta de todas as raças processada com sucesso. Total: {}", list.size());
                        }
                    });
        }
    }

    public CompletableFuture<Object> handle(GetBreedByIdQuery query) {
        logger.debug("Processando consulta de raça por ID: {}. Include images: {}", query.getBreedId(), query.isIncludeImages());

        if (query.isIncludeImages()) {
            return breedQueryService.getBreedWithImagesById(query.getBreedId())
                    .thenApply(breed -> (Object) breed)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.error("Erro ao processar consulta de raça com imagens por ID {}: ", query.getBreedId(), throwable);
                        } else {
                            @SuppressWarnings("unchecked")
                            Optional<?> opt = (Optional<?>) result;
                            logger.debug("Consulta de raça com imagens por ID processada: presente={}", opt.isPresent());
                        }
                    });
        } else {
            return CompletableFuture.completedFuture((Object) breedQueryService.getBreedById(query.getBreedId()))
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.error("Erro ao processar consulta de raça por ID {}: ", query.getBreedId(), throwable);
                        } else {
                            @SuppressWarnings("unchecked")
                            Optional<?> opt = (Optional<?>) result;
                            logger.debug("Consulta de raça por ID processada: presente={}", opt.isPresent());
                        }
                    });
        }
    }

    public CompletableFuture<Object> handle(GetBreedsByTemperamentQuery query) {
        logger.debug("Processando consulta de raças por temperamento: {}. Include images: {}", 
                    query.getTemperament(), query.isIncludeImages());

        if (query.isIncludeImages()) {
            return breedQueryService.getBreedsByTemperamentWithImages(query.getTemperament())
                    .thenApply(breeds -> (Object) breeds)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.error("Erro ao processar consulta de raças com imagens por temperamento {}: ", query.getTemperament(), throwable);
                        } else {
                            @SuppressWarnings("unchecked")
                            List<?> list = (List<?>) result;
                            logger.debug("Consulta de raças com imagens por temperamento processada. Total: {}", list.size());
                        }
                    });
        } else {
            return breedQueryService.getBreedsByTemperament(query.getTemperament())
                    .thenApply(breeds -> (Object) breeds)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.error("Erro ao processar consulta de raças por temperamento {}: ", query.getTemperament(), throwable);
                        } else {
                            @SuppressWarnings("unchecked")
                            List<?> list = (List<?>) result;
                            logger.debug("Consulta de raças por temperamento processada. Total: {}", list.size());
                        }
                    });
        }
    }

    public CompletableFuture<Object> handle(GetBreedsByOriginQuery query) {
        logger.debug("Processando consulta de raças por origem: {}. Include images: {}", 
                    query.getOrigin(), query.isIncludeImages());

        if (query.isIncludeImages()) {
            return breedQueryService.getBreedsByOriginWithImages(query.getOrigin())
                    .thenApply(breeds -> (Object) breeds)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.error("Erro ao processar consulta de raças com imagens por origem {}: ", query.getOrigin(), throwable);
                        } else {
                            @SuppressWarnings("unchecked")
                            List<?> list = (List<?>) result;
                            logger.debug("Consulta de raças com imagens por origem processada. Total: {}", list.size());
                        }
                    });
        } else {
            return breedQueryService.getBreedsByOrigin(query.getOrigin())
                    .thenApply(breeds -> (Object) breeds)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.error("Erro ao processar consulta de raças por origem {}: ", query.getOrigin(), throwable);
                        } else {
                            @SuppressWarnings("unchecked")
                            List<?> list = (List<?>) result;
                            logger.debug("Consulta de raças por origem processada. Total: {}", list.size());
                        }
                    });
        }
    }
}
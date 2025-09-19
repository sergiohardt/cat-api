package com.sencon.catapi.application.command.handler;

import com.sencon.catapi.application.command.dto.CollectBreedsCommand;
import com.sencon.catapi.application.command.service.BreedCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CollectBreedsCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(CollectBreedsCommandHandler.class);

    private final BreedCollectionService breedCollectionService;

    public CollectBreedsCommandHandler(BreedCollectionService breedCollectionService) {
        this.breedCollectionService = breedCollectionService;
    }

    public CompletableFuture<Integer> handle(CollectBreedsCommand command) {
        logger.info("Processando comando de coleta de raças");
        
        return breedCollectionService.collectBreeds(command.isForceUpdate())
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Erro ao processar comando de coleta de raças: ", throwable);
                    } else {
                        logger.info("Comando de coleta de raças processado com sucesso. Raças processadas: {}", result);
                    }
                });
    }
}

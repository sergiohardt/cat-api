package com.sencon.catapi.presentation.controller;

import com.sencon.catapi.application.command.dto.CollectBreedsCommand;
import com.sencon.catapi.application.command.dto.CollectImagesCommand;
import com.sencon.catapi.application.command.handler.CollectBreedsCommandHandler;
import com.sencon.catapi.application.command.handler.CollectImagesCommandHandler;
import com.sencon.catapi.presentation.dto.ApiResponse;
import com.sencon.catapi.presentation.dto.CollectionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/commands")
@Tag(name = "Commands", description = "Endpoints para coleta de dados da API externa")
public class CommandController {

    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

    private final CollectBreedsCommandHandler collectBreedsHandler;
    private final CollectImagesCommandHandler collectImagesHandler;

    public CommandController(CollectBreedsCommandHandler collectBreedsHandler,
                           CollectImagesCommandHandler collectImagesHandler) {
        this.collectBreedsHandler = collectBreedsHandler;
        this.collectImagesHandler = collectImagesHandler;
    }

    @PostMapping("/collect-breeds")
    @Operation(summary = "Coletar raças de gatos", 
               description = "Inicia o processo de coleta de raças de gatos da API externa")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Coleta iniciada com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> collectBreeds(
            @RequestParam(defaultValue = "false") boolean forceUpdate) {
        
        logger.info("Requisição para coleta de raças recebida. Force update: {}", forceUpdate);
        
        CollectBreedsCommand command = new CollectBreedsCommand(forceUpdate);
        
        return collectBreedsHandler.handle(command)
                .thenApply(result -> {
                    String message = String.format("Coleta de raças concluída. %d raças processadas.", result);
                    return ResponseEntity.ok(ApiResponse.success(message, "Processo concluído"));
                })
                .exceptionally(throwable -> {
                    logger.error("Erro durante coleta de raças: ", throwable);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error("Erro durante a coleta de raças: " + throwable.getMessage()));
                });
    }

    @PostMapping("/collect-images")
    @Operation(summary = "Coletar imagens de gatos", 
               description = "Inicia o processo de coleta de imagens de gatos da API externa")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Coleta iniciada com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> collectImages(
            @Valid @RequestBody CollectionRequest request) {
        
        logger.info("Requisição para coleta de imagens recebida: {}", request);
        
        CollectImagesCommand command = new CollectImagesCommand(
                request.isCollectBreedImages(),
                request.isCollectHatImages(),
                request.isCollectSunglassesImages(),
                request.getImagesPerBreed(),
                request.getSpecialImagesCount()
        );
        
        return collectImagesHandler.handle(command)
                .thenApply(result -> {
                    String message = String.format("Coleta de imagens concluída. %d imagens coletadas em %d operações.", 
                                                  result.getTotalImages(), result.getCollectionsExecuted());
                    return ResponseEntity.ok(ApiResponse.success(message, "Processo concluído"));
                })
                .exceptionally(throwable -> {
                    logger.error("Erro durante coleta de imagens: ", throwable);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error("Erro durante a coleta de imagens: " + throwable.getMessage()));
                });
    }

    @PostMapping("/collect-all")
    @Operation(summary = "Coletar tudo", 
               description = "Inicia o processo completo de coleta de raças e imagens")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Coleta completa iniciada com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> collectAll(
            @Valid @RequestBody CollectionRequest request) {
        
        logger.info("Requisição para coleta completa recebida: {}", request);
        
        CollectBreedsCommand breedsCommand = new CollectBreedsCommand(request.isForceUpdate());
        CollectImagesCommand imagesCommand = new CollectImagesCommand(
                request.isCollectBreedImages(),
                request.isCollectHatImages(),
                request.isCollectSunglassesImages(),
                request.getImagesPerBreed(),
                request.getSpecialImagesCount()
        );
        
        return collectBreedsHandler.handle(breedsCommand)
                .thenCompose(breedsResult -> {
                    logger.info("Coleta de raças concluída, iniciando coleta de imagens");
                    return collectImagesHandler.handle(imagesCommand)
                            .thenApply(imagesResult -> new Object[]{breedsResult, imagesResult});
                })
                .thenApply(results -> {
                    Integer breedsCount = (Integer) results[0];
                    CollectImagesCommandHandler.CollectionResult imagesResult = 
                            (CollectImagesCommandHandler.CollectionResult) results[1];
                    
                    String message = String.format("Coleta completa concluída. %d raças e %d imagens processadas.", 
                                                  breedsCount, imagesResult.getTotalImages());
                    return ResponseEntity.ok(ApiResponse.success(message, "Processo completo concluído"));
                })
                .exceptionally(throwable -> {
                    logger.error("Erro durante coleta completa: ", throwable);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error("Erro durante a coleta completa: " + throwable.getMessage()));
                });
    }
}

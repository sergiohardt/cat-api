package com.sencon.catapi.presentation.controller;

import com.sencon.catapi.infrastructure.messaging.SqsService;
import com.sencon.catapi.presentation.dto.ApiResponse;
import com.sencon.catapi.presentation.dto.AsyncBreedByIdRequest;
import com.sencon.catapi.presentation.dto.AsyncBreedByOriginRequest;
import com.sencon.catapi.presentation.dto.AsyncBreedByTemperamentRequest;
import com.sencon.catapi.presentation.dto.AsyncBreedRequest;
import com.sencon.catapi.presentation.dto.AsyncProcessingMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/async/breeds")
@Validated
@Tag(name = "Async Queries", description = "Endpoints para consultas assíncronas de dados das raças de gatos")
public class AsyncQueryController {

    private static final Logger logger = LoggerFactory.getLogger(AsyncQueryController.class);

    private final SqsService sqsService;

    public AsyncQueryController(SqsService sqsService) {
        this.sqsService = sqsService;
    }

    @PostMapping("/all")
    @Operation(summary = "Solicitar lista de todas as raças (assíncrono)", 
               description = "Envia requisição para processamento assíncrono. Os resultados serão enviados por email.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Requisição aceita para processamento"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> getAllBreedsAsync(
            @Parameter(description = "Dados da requisição assíncrona", required = true)
            @RequestBody @Valid AsyncBreedRequest request) {
        
        logger.info("Requisição assíncrona para listar todas as raças. Email: {}, Include images: {}", 
                   request.email(), request.includeImages());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                AsyncProcessingMessage message = AsyncProcessingMessage.getAllBreeds(
                    request.email(), request.includeImages());
                
                String messageId = sqsService.sendMessage(message);
                
                return ResponseEntity.accepted()
                        .body(ApiResponse.success(
                            "Requisição aceita para processamento. Você receberá os resultados por email.",
                            new ProcessingInfo(message.requestId(), messageId, request.email())
                        ));
                
            } catch (Exception e) {
                logger.error("Erro ao processar requisição assíncrona para todas as raças: ", e);
                return ResponseEntity.internalServerError()
                        .body(ApiResponse.error("Erro ao processar requisição: " + e.getMessage()));
            }
        });
    }

    @PostMapping("/by-id")
    @Operation(summary = "Solicitar raça por ID (assíncrono)", 
               description = "Envia requisição para buscar raça por ID de forma assíncrona. Os resultados serão enviados por email.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Requisição aceita para processamento"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> getBreedByIdAsync(
            @Parameter(description = "Dados da requisição assíncrona", required = true)
            @RequestBody @Valid AsyncBreedByIdRequest request) {
        
        logger.info("Requisição assíncrona para buscar raça por ID: {}. Email: {}, Include images: {}", 
                   request.breedId(), request.email(), request.includeImages());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                AsyncProcessingMessage message = AsyncProcessingMessage.getBreedById(
                    request.email(), request.breedId(), request.includeImages());
                
                String messageId = sqsService.sendMessage(message);
                
                return ResponseEntity.accepted()
                        .body(ApiResponse.success(
                            "Requisição aceita para processamento. Você receberá os resultados por email.",
                            new ProcessingInfo(message.requestId(), messageId, request.email())
                        ));
                
            } catch (Exception e) {
                logger.error("Erro ao processar requisição assíncrona para raça por ID {}: ", request.breedId(), e);
                return ResponseEntity.internalServerError()
                        .body(ApiResponse.error("Erro ao processar requisição: " + e.getMessage()));
            }
        });
    }

    @PostMapping("/by-temperament")
    @Operation(summary = "Solicitar raças por temperamento (assíncrono)", 
               description = "Envia requisição para buscar raças por temperamento de forma assíncrona. Os resultados serão enviados por email.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Requisição aceita para processamento"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> getBreedsByTemperamentAsync(
            @Parameter(description = "Dados da requisição assíncrona", required = true)
            @RequestBody @Valid AsyncBreedByTemperamentRequest request) {
        
        logger.info("Requisição assíncrona para buscar raças por temperamento: {}. Email: {}, Include images: {}", 
                   request.temperament(), request.email(), request.includeImages());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                AsyncProcessingMessage message = AsyncProcessingMessage.getBreedsByTemperament(
                    request.email(), request.temperament(), request.includeImages());
                
                String messageId = sqsService.sendMessage(message);
                
                return ResponseEntity.accepted()
                        .body(ApiResponse.success(
                            "Requisição aceita para processamento. Você receberá os resultados por email.",
                            new ProcessingInfo(message.requestId(), messageId, request.email())
                        ));
                
            } catch (Exception e) {
                logger.error("Erro ao processar requisição assíncrona para raças por temperamento {}: ", 
                           request.temperament(), e);
                return ResponseEntity.internalServerError()
                        .body(ApiResponse.error("Erro ao processar requisição: " + e.getMessage()));
            }
        });
    }

    @PostMapping("/by-origin")
    @Operation(summary = "Solicitar raças por origem (assíncrono)", 
               description = "Envia requisição para buscar raças por origem de forma assíncrona. Os resultados serão enviados por email.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Requisição aceita para processamento"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> getBreedsByOriginAsync(
            @Parameter(description = "Dados da requisição assíncrona", required = true)
            @RequestBody @Valid AsyncBreedByOriginRequest request) {
        
        logger.info("Requisição assíncrona para buscar raças por origem: {}. Email: {}, Include images: {}", 
                   request.origin(), request.email(), request.includeImages());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                AsyncProcessingMessage message = AsyncProcessingMessage.getBreedsByOrigin(
                    request.email(), request.origin(), request.includeImages());
                
                String messageId = sqsService.sendMessage(message);
                
                return ResponseEntity.accepted()
                        .body(ApiResponse.success(
                            "Requisição aceita para processamento. Você receberá os resultados por email.",
                            new ProcessingInfo(message.requestId(), messageId, request.email())
                        ));
                
            } catch (Exception e) {
                logger.error("Erro ao processar requisição assíncrona para raças por origem {}: ", 
                           request.origin(), e);
                return ResponseEntity.internalServerError()
                        .body(ApiResponse.error("Erro ao processar requisição: " + e.getMessage()));
            }
        });
    }

    public record ProcessingInfo(String requestId, String messageId, String notificationEmail) {}
}

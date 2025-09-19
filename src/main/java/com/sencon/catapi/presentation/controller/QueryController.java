package com.sencon.catapi.presentation.controller;

import com.sencon.catapi.application.query.dto.*;
import com.sencon.catapi.application.query.handler.BreedQueryHandler;
import com.sencon.catapi.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/breeds")
@Validated
@Tag(name = "Queries", description = "Endpoints para consulta de dados das raças de gatos")
public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    private final BreedQueryHandler breedQueryHandler;

    public QueryController(BreedQueryHandler breedQueryHandler) {
        this.breedQueryHandler = breedQueryHandler;
    }

    @GetMapping
    @Operation(summary = "Listar todas as raças", 
               description = "Retorna uma lista com todas as raças de gatos cadastradas")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @SuppressWarnings("unchecked")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> getAllBreeds(
            @Parameter(description = "Incluir imagens na resposta")
            @RequestParam(defaultValue = "false") boolean includeImages,
            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direção da ordenação")
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        logger.info("Requisição para listar todas as raças. Include images: {}, Sort: {} {}", 
                   includeImages, sortBy, sortDirection);
        
        GetAllBreedsQuery query = new GetAllBreedsQuery(includeImages, sortBy, sortDirection);
        
        return breedQueryHandler.handle(query)
                .thenApply(breeds -> {
                    List<?> list = (List<?>) breeds;
                    String message = String.format("Lista de raças retornada com sucesso. Total: %d", list.size());
                    return ResponseEntity.ok(ApiResponse.success(message, breeds));
                })
                .exceptionally(throwable -> {
                    logger.error("Erro ao buscar todas as raças: ", throwable);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error("Erro ao buscar raças: " + throwable.getMessage()));
                });
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar raça por ID", 
               description = "Retorna as informações de uma raça específica pelo seu ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Raça encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Raça não encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @SuppressWarnings("unchecked")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> getBreedById(
            @Parameter(description = "ID da raça")
            @PathVariable UUID id,
            @Parameter(description = "Incluir imagens na resposta")
            @RequestParam(defaultValue = "false") boolean includeImages) {
        
        logger.info("Requisição para buscar raça por ID: {}. Include images: {}", id, includeImages);
        
        GetBreedByIdQuery query = new GetBreedByIdQuery(id, includeImages);
        
        return breedQueryHandler.handle(query)
                .thenApply(result -> {
                    Optional<?> optionalBreed = (Optional<?>) result;
                    if (optionalBreed.isPresent()) {
                        return ResponseEntity.ok(ApiResponse.success("Raça encontrada", optionalBreed.get()));
                    } else {
                        return ResponseEntity.notFound().<ApiResponse<Object>>build();
                    }
                })
                .exceptionally(throwable -> {
                    logger.error("Erro ao buscar raça por ID {}: ", id, throwable);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error("Erro ao buscar raça: " + throwable.getMessage()));
                });
    }

    @GetMapping("/by-temperament")
    @Operation(summary = "Buscar raças por temperamento", 
               description = "Retorna uma lista de raças que possuem um temperamento específico")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Temperamento não informado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @SuppressWarnings("unchecked")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> getBreedsByTemperament(
            @Parameter(description = "Temperamento a ser buscado", required = true)
            @RequestParam @NotBlank(message = "Temperamento é obrigatório") String temperament,
            @Parameter(description = "Incluir imagens na resposta")
            @RequestParam(defaultValue = "false") boolean includeImages) {
        
        logger.info("Requisição para buscar raças por temperamento: {}. Include images: {}", 
                   temperament, includeImages);
        
        GetBreedsByTemperamentQuery query = new GetBreedsByTemperamentQuery(temperament, includeImages);
        
        return breedQueryHandler.handle(query)
                .thenApply(breeds -> {
                    List<?> list = (List<?>) breeds;
                    String message = String.format("Raças encontradas para o temperamento '%s'. Total: %d", 
                                                  temperament, list.size());
                    return ResponseEntity.ok(ApiResponse.success(message, breeds));
                })
                .exceptionally(throwable -> {
                    logger.error("Erro ao buscar raças por temperamento {}: ", temperament, throwable);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error("Erro ao buscar raças por temperamento: " + throwable.getMessage()));
                });
    }

    @GetMapping("/by-origin")
    @Operation(summary = "Buscar raças por origem", 
               description = "Retorna uma lista de raças que possuem uma origem específica")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Origem não informada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @SuppressWarnings("unchecked")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> getBreedsByOrigin(
            @Parameter(description = "Origem a ser buscada", required = true)
            @RequestParam @NotBlank(message = "Origem é obrigatória") String origin,
            @Parameter(description = "Incluir imagens na resposta")
            @RequestParam(defaultValue = "false") boolean includeImages) {
        
        logger.info("Requisição para buscar raças por origem: {}. Include images: {}", 
                   origin, includeImages);
        
        GetBreedsByOriginQuery query = new GetBreedsByOriginQuery(origin, includeImages);
        
        return breedQueryHandler.handle(query)
                .thenApply(breeds -> {
                    List<?> list = (List<?>) breeds;
                    String message = String.format("Raças encontradas para a origem '%s'. Total: %d", 
                                                  origin, list.size());
                    return ResponseEntity.ok(ApiResponse.success(message, breeds));
                })
                .exceptionally(throwable -> {
                    logger.error("Erro ao buscar raças por origem {}: ", origin, throwable);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error("Erro ao buscar raças por origem: " + throwable.getMessage()));
                });
    }
}

package com.sencon.catapi.infrastructure.external.client;

import com.sencon.catapi.infrastructure.external.dto.CatApiBreedDto;
import com.sencon.catapi.infrastructure.external.dto.CatApiImageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Component
public class CatApiClient {

    private static final Logger logger = LoggerFactory.getLogger(CatApiClient.class);

    private final WebClient webClient;
    private final int maxRetries;

    public CatApiClient(WebClient catApiWebClient, @Value("${cat.api.max-retries:3}") int maxRetries) {
        this.webClient = catApiWebClient;
        this.maxRetries = maxRetries;
    }

    public Flux<CatApiBreedDto> getAllBreeds() {
        logger.debug("Buscando todas as raças de gatos");
        
        return webClient.get()
                .uri("/breeds")
                .retrieve()
                .bodyToFlux(CatApiBreedDto.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofSeconds(1)))
                .doOnNext(breed -> logger.debug("Raça recebida: {}", breed.getName()))
                .doOnError(error -> logger.error("Erro ao buscar raças: ", error));
    }

    public Flux<CatApiImageDto> getImagesByBreed(String breedId, int limit) {
        logger.debug("Buscando {} imagens para a raça: {}", limit, breedId);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/images/search")
                        .queryParam("breed_ids", breedId)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToFlux(CatApiImageDto.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofSeconds(1)))
                .doOnNext(image -> logger.debug("Imagem da raça recebida: {}", image.getUrl()))
                .doOnError(error -> logger.error("Erro ao buscar imagens da raça {}: ", breedId, error));
    }

    public Flux<CatApiImageDto> getImagesByCategory(int categoryId, int limit) {
        logger.debug("Buscando {} imagens para a categoria: {}", limit, categoryId);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/images/search")
                        .queryParam("category_ids", categoryId)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToFlux(CatApiImageDto.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofSeconds(1)))
                .doOnNext(image -> logger.debug("Imagem da categoria recebida: {}", image.getUrl()))
                .doOnError(error -> logger.error("Erro ao buscar imagens da categoria {}: ", categoryId, error));
    }

    public Mono<List<CatApiImageDto>> getImagesWithHats(int limit) {
        logger.debug("Buscando {} imagens de gatos com chapéu", limit);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/images/search")
                        .queryParam("category_ids", "1")
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CatApiImageDto>>() {})
                .retryWhen(Retry.backoff(maxRetries, Duration.ofSeconds(1)))
                .doOnNext(images -> logger.debug("Recebidas {} imagens com chapéu", images.size()))
                .doOnError(error -> logger.error("Erro ao buscar imagens com chapéu: ", error));
    }

    public Mono<List<CatApiImageDto>> getImagesWithSunglasses(int limit) {
        logger.debug("Buscando {} imagens de gatos com óculos", limit);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/images/search")
                        .queryParam("category_ids", "4")
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CatApiImageDto>>() {})
                .retryWhen(Retry.backoff(maxRetries, Duration.ofSeconds(1)))
                .doOnNext(images -> logger.debug("Recebidas {} imagens com óculos", images.size()))
                .doOnError(error -> logger.error("Erro ao buscar imagens com óculos: ", error));
    }
}

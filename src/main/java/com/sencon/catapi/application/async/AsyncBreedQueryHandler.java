package com.sencon.catapi.application.async;

import com.sencon.catapi.application.query.dto.*;
import com.sencon.catapi.application.query.handler.BreedQueryHandler;
import com.sencon.catapi.infrastructure.email.EmailService;
import com.sencon.catapi.infrastructure.email.EmailTemplateService;
import com.sencon.catapi.presentation.dto.AsyncProcessingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncBreedQueryHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncBreedQueryHandler.class);
    
    private final BreedQueryHandler breedQueryHandler;
    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;
    
    public AsyncBreedQueryHandler(BreedQueryHandler breedQueryHandler, 
                                 EmailService emailService,
                                 EmailTemplateService emailTemplateService) {
        this.breedQueryHandler = breedQueryHandler;
        this.emailService = emailService;
        this.emailTemplateService = emailTemplateService;
    }
    
    public CompletableFuture<Void> processAsyncRequest(AsyncProcessingMessage message) {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Processando requisição assíncrona: {}", message.requestId());
                
                Object result = executeQuery(message);
                sendEmailWithResults(message, result);
                
                logger.info("Requisição assíncrona processada com sucesso: {}", message.requestId());
                
            } catch (Exception e) {
                logger.error("Erro ao processar requisição assíncrona {}: ", message.requestId(), e);
                sendErrorEmail(message, e);
            }
        });
    }
    
    private Object executeQuery(AsyncProcessingMessage message) throws Exception {
        Map<String, String> params = parseQueryParameters(message.queryParameters());
        
        return switch (message.requestType()) {
            case "GET_ALL_BREEDS" -> {
                boolean includeImages = Boolean.parseBoolean(params.getOrDefault("includeImages", "false"));
                String sortBy = params.getOrDefault("sortBy", "name");
                String sortDirection = params.getOrDefault("sortDirection", "ASC");
                
                GetAllBreedsQuery query = new GetAllBreedsQuery(includeImages, sortBy, sortDirection);
                yield breedQueryHandler.handle(query).get();
            }
            case "GET_BREED_BY_ID" -> {
                UUID breedId = UUID.fromString(params.get("breedId"));
                boolean includeImages = Boolean.parseBoolean(params.getOrDefault("includeImages", "false"));
                
                GetBreedByIdQuery query = new GetBreedByIdQuery(breedId, includeImages);
                Object result = breedQueryHandler.handle(query).get();
                
                if (result instanceof Optional<?> optional) {
                    yield optional.orElse(null);
                }
                yield result;
            }
            case "GET_BREEDS_BY_TEMPERAMENT" -> {
                String temperament = URLDecoder.decode(params.get("temperament"), StandardCharsets.UTF_8);
                boolean includeImages = Boolean.parseBoolean(params.getOrDefault("includeImages", "false"));
                
                GetBreedsByTemperamentQuery query = new GetBreedsByTemperamentQuery(temperament, includeImages);
                yield breedQueryHandler.handle(query).get();
            }
            case "GET_BREEDS_BY_ORIGIN" -> {
                String origin = URLDecoder.decode(params.get("origin"), StandardCharsets.UTF_8);
                boolean includeImages = Boolean.parseBoolean(params.getOrDefault("includeImages", "false"));
                
                GetBreedsByOriginQuery query = new GetBreedsByOriginQuery(origin, includeImages);
                yield breedQueryHandler.handle(query).get();
            }
            default -> throw new IllegalArgumentException("Tipo de requisição não suportado: " + message.requestType());
        };
    }
    
    private void sendEmailWithResults(AsyncProcessingMessage message, Object result) {
        String subject = "Resultados da sua consulta - Cat API";
        String htmlContent = emailTemplateService.generateBreedResultsHtml(
            message.requestType(), result, message.requestId());
        String textContent = emailTemplateService.generateBreedResultsText(
            message.requestType(), result, message.requestId());
        
        emailService.sendEmail(message.email(), subject, htmlContent, textContent);
    }
    
    private void sendErrorEmail(AsyncProcessingMessage message, Exception error) {
        try {
            String subject = "Erro no processamento da sua consulta - Cat API";
            String htmlContent = generateErrorHtml(message, error);
            String textContent = generateErrorText(message, error);
            
            emailService.sendEmail(message.email(), subject, htmlContent, textContent);
        } catch (Exception e) {
            logger.error("Erro ao enviar email de erro para {}: ", message.email(), e);
        }
    }
    
    private String generateErrorHtml(AsyncProcessingMessage message, Exception error) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset='UTF-8'>
                <title>Erro no Processamento - Cat API</title>
            </head>
            <body>
                <h1>❌ Erro no processamento da sua consulta</h1>
                <p><strong>ID da requisição:</strong> %s</p>
                <p><strong>Erro:</strong> %s</p>
                <p>Por favor, tente novamente ou entre em contato com o suporte.</p>
            </body>
            </html>
            """, message.requestId(), error.getMessage());
    }
    
    private String generateErrorText(AsyncProcessingMessage message, Exception error) {
        return String.format("""
            === Erro no processamento da sua consulta - Cat API ===
            
            ID da requisição: %s
            Erro: %s
            
            Por favor, tente novamente ou entre em contato com o suporte.
            """, message.requestId(), error.getMessage());
    }
    
    private Map<String, String> parseQueryParameters(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }
        
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        
        return params;
    }
}

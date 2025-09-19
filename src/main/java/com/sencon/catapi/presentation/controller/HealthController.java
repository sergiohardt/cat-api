package com.sencon.catapi.presentation.controller;

import com.sencon.catapi.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Endpoints para verificação de saúde da aplicação")
public class HealthController {

    @GetMapping
    @Operation(summary = "Verificar saúde da aplicação", 
               description = "Retorna o status de saúde da aplicação")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthData = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "application", "cat-api",
                "version", "1.0.0"
        );
        
        return ResponseEntity.ok(ApiResponse.success("Aplicação funcionando corretamente", healthData));
    }
}

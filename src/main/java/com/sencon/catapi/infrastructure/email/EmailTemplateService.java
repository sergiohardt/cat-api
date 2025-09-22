package com.sencon.catapi.infrastructure.email;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {
    
    public String generateBreedResultsHtml(String requestType, Object data, String requestId) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>")
            .append("<html>")
            .append("<head>")
            .append("<meta charset='UTF-8'>")
            .append("<title>Resultados da Consulta - Cat API</title>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; margin: 20px; }")
            .append("h1 { color: #2c3e50; }")
            .append("h2 { color: #34495e; }")
            .append(".breed { margin: 15px 0; padding: 10px; border: 1px solid #bdc3c7; border-radius: 5px; }")
            .append(".image-url { color: #3498db; word-break: break-all; }")
            .append(".footer { margin-top: 30px; font-size: 12px; color: #7f8c8d; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<h1>🐱 Resultados da sua consulta - Cat API</h1>")
            .append("<p><strong>Tipo de consulta:</strong> ").append(getRequestTypeDescription(requestType)).append("</p>")
            .append("<p><strong>ID da requisição:</strong> ").append(requestId).append("</p>")
            .append("<h2>Resultados:</h2>")
            .append("<div>")
            .append(formatDataAsHtml(data))
            .append("</div>")
            .append("<div class='footer'>")
            .append("<p>Este email foi enviado automaticamente pelo sistema Cat API.</p>")
            .append("<p>Data: ").append(java.time.LocalDateTime.now()).append("</p>")
            .append("</div>")
            .append("</body>")
            .append("</html>");
        
        return html.toString();
    }
    
    public String generateBreedResultsText(String requestType, Object data, String requestId) {
        StringBuilder text = new StringBuilder();
        
        text.append("=== Resultados da sua consulta - Cat API ===\n\n")
            .append("Tipo de consulta: ").append(getRequestTypeDescription(requestType)).append("\n")
            .append("ID da requisição: ").append(requestId).append("\n\n")
            .append("Resultados:\n")
            .append(formatDataAsText(data))
            .append("\n\n")
            .append("---\n")
            .append("Este email foi enviado automaticamente pelo sistema Cat API.\n")
            .append("Data: ").append(java.time.LocalDateTime.now()).append("\n");
        
        return text.toString();
    }
    
    private String getRequestTypeDescription(String requestType) {
        return switch (requestType) {
            case "GET_ALL_BREEDS" -> "Listar todas as raças";
            case "GET_BREED_BY_ID" -> "Buscar raça por ID";
            case "GET_BREEDS_BY_TEMPERAMENT" -> "Buscar raças por temperamento";
            case "GET_BREEDS_BY_ORIGIN" -> "Buscar raças por origem";
            default -> "Consulta não identificada";
        };
    }
    
    private String formatDataAsHtml(Object data) {
        if (data == null) {
            return "<p>Nenhum resultado encontrado.</p>";
        }
        
        return "<pre style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; overflow-x: auto;'>" 
               + data.toString() + "</pre>";
    }
    
    private String formatDataAsText(Object data) {
        if (data == null) {
            return "Nenhum resultado encontrado.";
        }
        
        return data.toString();
    }
}

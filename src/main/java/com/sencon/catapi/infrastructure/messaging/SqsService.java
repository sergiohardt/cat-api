package com.sencon.catapi.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sencon.catapi.presentation.dto.AsyncProcessingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
public class SqsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SqsService.class);
    
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    
    @Value("${aws.sqs.queue.url}")
    private String queueUrl;
    
    public SqsService(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }
    
    public String sendMessage(AsyncProcessingMessage message) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);
            
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .delaySeconds(0)
                    .build();
            
            SendMessageResponse response = sqsClient.sendMessage(sendMsgRequest);
            
            logger.info("Mensagem enviada para SQS. MessageId: {}, RequestId: {}", 
                       response.messageId(), message.requestId());
            
            return response.messageId();
            
        } catch (JsonProcessingException e) {
            logger.error("Erro ao serializar mensagem para SQS: {}", message, e);
            throw new RuntimeException("Erro ao enviar mensagem para fila", e);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para SQS: {}", message, e);
            throw new RuntimeException("Erro ao enviar mensagem para fila", e);
        }
    }
}

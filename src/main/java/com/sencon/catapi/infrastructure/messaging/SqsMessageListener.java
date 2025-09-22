package com.sencon.catapi.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sencon.catapi.application.async.AsyncBreedQueryHandler;
import com.sencon.catapi.presentation.dto.AsyncProcessingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;

@Component
public class SqsMessageListener {
    
    private static final Logger logger = LoggerFactory.getLogger(SqsMessageListener.class);
    
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final AsyncBreedQueryHandler asyncBreedQueryHandler;
    
    @Value("${aws.sqs.queue.url}")
    private String queueUrl;
    
    public SqsMessageListener(SqsClient sqsClient, 
                             ObjectMapper objectMapper,
                             AsyncBreedQueryHandler asyncBreedQueryHandler) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.asyncBreedQueryHandler = asyncBreedQueryHandler;
    }
    
    @Scheduled(fixedDelay = 5000)
    public void pollMessages() {
        try {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(20)
                    .build();
            
            ReceiveMessageResponse response = sqsClient.receiveMessage(receiveRequest);
            List<Message> messages = response.messages();
            
            if (!messages.isEmpty()) {
                logger.info("Recebidas {} mensagens da fila SQS", messages.size());
            }
            
            for (Message message : messages) {
                processMessage(message);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao receber mensagens da fila SQS: ", e);
        }
    }
    
    private void processMessage(Message sqsMessage) {
        try {
            String messageBody = sqsMessage.body();
            AsyncProcessingMessage processingMessage = objectMapper.readValue(messageBody, AsyncProcessingMessage.class);
            
            logger.info("Processando mensagem: RequestId={}, Type={}", 
                       processingMessage.requestId(), processingMessage.requestType());
            
            asyncBreedQueryHandler.processAsyncRequest(processingMessage)
                    .thenRun(() -> deleteMessage(sqsMessage))
                    .exceptionally(throwable -> {
                        logger.error("Erro ao processar mensagem {}: ", processingMessage.requestId(), throwable);
                        deleteMessage(sqsMessage);
                        return null;
                    });
            
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem SQS: {}", sqsMessage.body(), e);
            deleteMessage(sqsMessage);
        }
    }
    
    private void deleteMessage(Message message) {
        try {
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();
            
            sqsClient.deleteMessage(deleteRequest);
            
            logger.debug("Mensagem removida da fila: {}", message.messageId());
            
        } catch (Exception e) {
            logger.error("Erro ao remover mensagem da fila: {}", message.messageId(), e);
        }
    }
}

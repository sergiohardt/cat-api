package com.sencon.catapi.infrastructure.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final SesClient sesClient;
    
    @Value("${aws.ses.from-email}")
    private String fromEmail;
    
    public EmailService(SesClient sesClient) {
        this.sesClient = sesClient;
    }
    
    public void sendEmail(String toEmail, String subject, String htmlContent, String textContent) {
        try {
            Destination destination = Destination.builder()
                    .toAddresses(toEmail)
                    .build();
            
            Content subjectContent = Content.builder()
                    .data(subject)
                    .charset("UTF-8")
                    .build();
            
            Content htmlBody = Content.builder()
                    .data(htmlContent)
                    .charset("UTF-8")
                    .build();
            
            Content textBody = Content.builder()
                    .data(textContent)
                    .charset("UTF-8")
                    .build();
            
            Body body = Body.builder()
                    .html(htmlBody)
                    .text(textBody)
                    .build();
            
            Message message = Message.builder()
                    .subject(subjectContent)
                    .body(body)
                    .build();
            
            SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                    .source(fromEmail)
                    .destination(destination)
                    .message(message)
                    .build();
            
            SendEmailResponse response = sesClient.sendEmail(sendEmailRequest);
            
            logger.info("Email enviado com sucesso. MessageId: {}, To: {}", 
                       response.messageId(), toEmail);
            
        } catch (Exception e) {
            logger.error("Erro ao enviar email para {}: ", toEmail, e);
            throw new RuntimeException("Erro ao enviar email", e);
        }
    }
}

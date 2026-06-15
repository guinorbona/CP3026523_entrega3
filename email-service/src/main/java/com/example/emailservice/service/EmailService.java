package com.example.emailservice.service;

import com.example.emailservice.dto.EmailRecordDto;
import com.example.emailservice.enums.StatusEmail;
import com.example.emailservice.model.EmailModel;
import com.example.emailservice.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private final EmailRepository emailRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public EmailService(EmailRepository emailRepository, JavaMailSender javaMailSender) {
        this.emailRepository = emailRepository;
        this.javaMailSender = javaMailSender;
    }

    public EmailModel sendEmail(EmailRecordDto emailRecordDto) {
        EmailModel emailModel = new EmailModel();

        emailModel.setUserId(emailRecordDto.userId());
        emailModel.setEmailFrom(emailFrom);
        emailModel.setEmailTo(emailRecordDto.emailTo());
        emailModel.setSubject(emailRecordDto.subject());
        emailModel.setText(emailRecordDto.text());
        emailModel.setSendDateEmail(LocalDateTime.now());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(emailRecordDto.emailTo());
            message.setSubject(emailRecordDto.subject());
            message.setText(emailRecordDto.text());

            javaMailSender.send(message);

            emailModel.setStatus(StatusEmail.SENT);
        } catch (Exception exception) {
            emailModel.setStatus(StatusEmail.ERROR);
            System.out.println("Erro ao enviar e-mail: " + exception.getMessage());
        }

        return emailRepository.save(emailModel);
    }
}
package com.example.emailservice.model;

import com.example.emailservice.enums.StatusEmail;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "emails")
public class EmailModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID emailId;

    private UUID userId;

    private String emailFrom;

    private String emailTo;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String text;

    private LocalDateTime sendDateEmail;

    @Enumerated(EnumType.STRING)
    private StatusEmail status;
}
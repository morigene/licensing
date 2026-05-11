package rw.nbr.licensing.application.entity;

import jakarta.persistence.*;
import rw.nbr.licensing.application.type.ApplicationState;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications_license")
public class Application {

    @Id
    @GeneratedValue
    private UUID id;

    private String applicantUsername;

    @Enumerated(EnumType.STRING)
    private ApplicationState state;

    private UUID reviewedBy;
    private UUID approvedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

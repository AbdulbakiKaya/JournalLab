package journalLabb.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Encounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long practitionerUserId; // doctor userId

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String imageId;
    @Column(length = 4000)
    private String note;

    private String location; // ex "101A General Medicine"
}
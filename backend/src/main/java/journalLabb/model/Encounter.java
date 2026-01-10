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

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String note;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Practitioner practitioner;

    @ManyToOne
    private Location location;
}
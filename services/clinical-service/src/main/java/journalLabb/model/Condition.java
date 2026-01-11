package journalLabb.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "conditions") // <-- VIKTIGT: undvik reserverat ord
@Getter
@Setter
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long practitionerUserId;

    @Column(nullable = false, length = 512)
    private String text;

    @Column(length = 64)
    private String severity;

    private LocalDateTime createdAt;
}
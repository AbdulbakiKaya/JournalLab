package journalLabb.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String personalNumber;

    // Pekar på users.id i samma service (inte JPA relation)
    @Column(nullable = false, unique = true)
    private Long userId;

    // Vi lagrar läkarens userId här (senare kan practitioner-service mappa)
    @Column(nullable = false)
    private Long assignedDoctorId;
}

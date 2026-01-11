package journalLabb.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Practitioner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licenseNumber;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PractitionerType type;

    // Koppling via userId (samma pattern som Patient.userId)
    @Column(nullable = false, unique = true)
    private Long userId;
}
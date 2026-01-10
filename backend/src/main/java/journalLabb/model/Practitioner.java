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
    private PractitionerType type;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    private Organization organization;
}

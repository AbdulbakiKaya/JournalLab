package journalLabb.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Observation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String text;


    @ManyToOne
    private Patient patient;


    @ManyToOne
    private Practitioner practitioner;
}
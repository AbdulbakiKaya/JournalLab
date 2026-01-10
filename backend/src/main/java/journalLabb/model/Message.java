package journalLabb.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;
    private String text;
    private LocalDateTime timestamp;
    @ManyToOne
    private Patient patient;
    @Enumerated(EnumType.STRING)
    private MessageThreadType threadType;
}

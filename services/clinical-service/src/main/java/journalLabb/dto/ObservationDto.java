package journalLabb.dto;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ObservationDto {
    private String text;
    private Long patientId;
}
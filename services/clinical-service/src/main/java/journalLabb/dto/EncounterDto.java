package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EncounterDto {
    private Long id;
    private Long patientId;
    private Long practitionerUserId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String practitionerName;
    private String practitionerType;
    private String note;
    private String location;
    private String imageId;
}
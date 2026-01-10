package journalLabb.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PatientDetailsDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Long userId;
    private List<Map<String, Object>> encounters;
    private List<Map<String, Object>> conditions;
    private Long assignedDoctorId;
    private String assignedDoctorName;
    private List<MessageDto> messages;
}

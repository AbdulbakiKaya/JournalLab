package journalLabb.service;

import journalLabb.model.Role;
import journalLabb.repository.PatientRepository;
import journalLabb.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PatientAccessService {

    private final PatientRepository patientRepository;

    public void assertDoctorCanWrite(UserPrincipal principal, Long patientId) {
        if (principal.getRole() == Role.DOCTOR) {
            Long doctorPractitionerId = principal.getPractitionerId();

            boolean ok = doctorPractitionerId != null &&
                    patientRepository.existsByIdAndAssignedDoctor_Id(patientId, doctorPractitionerId);

            if (!ok) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Doctor not assigned to this patient");
            }
        }
    }
}

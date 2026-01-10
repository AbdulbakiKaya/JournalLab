package journalLabb.controller;

import journalLabb.dto.MessageDto;
import journalLabb.dto.SendMessageDto;
import journalLabb.security.UserPrincipal;
import journalLabb.service.MessageService;
import journalLabb.service.PatientAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final PatientAccessService patientAccessService;

    @PostMapping
    public MessageDto sendMessage(
            @RequestBody SendMessageDto dto,
            Authentication authentication
    ) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        patientAccessService.assertDoctorCanWrite(principal, dto.getPatientId());

        return messageService.sendMessage(principal.getUserId(), dto);
    }

    @GetMapping("/patient/{patientId}/thread/{threadType}")
    public List<MessageDto> getPatientMessagesByThread(
            @PathVariable Long patientId,
            @PathVariable String threadType,
            Authentication authentication
    ) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return messageService.getMessagesForPatientByThread(principal.getUserId(), patientId, threadType);
    }

    @GetMapping("/patient/{patientId}")
    public List<MessageDto> getPatientMessages(@PathVariable Long patientId) {
        return messageService.getMessagesForPatient(patientId);
    }
}
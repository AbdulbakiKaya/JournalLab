package journalLabb.controller;

import journalLabb.dto.MessageDto;
import journalLabb.dto.SendMessageDto;
import journalLabb.model.Role;
import journalLabb.security.UserPrincipal;
import journalLabb.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public MessageDto sendMessage(@RequestBody SendMessageDto dto, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // Tillfälligt microservice-säkert: endast DOCTOR/STAFF får skriva
        if (principal.getRole() == Role.PATIENT) {
            throw new RuntimeException("Patient får inte skicka via detta endpoint i messaging-service (än).");
        }

        return messageService.sendMessage(principal.getUserId(), dto);
    }

    @GetMapping("/patient/{patientId}/thread/{threadType}")
    public List<MessageDto> getPatientMessagesByThread(
            @PathVariable Long patientId,
            @PathVariable String threadType,
            Authentication authentication
    ) {
        // Läsning: tillåt alla inloggade för nu
        return messageService.getMessagesForPatientByThread(patientId, threadType);
    }

    @GetMapping("/me")
    public List<MessageDto> getMyMessages(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return messageService.getMessagesForUser(principal.getUserId());
    }
}
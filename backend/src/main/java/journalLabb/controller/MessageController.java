package journalLabb.controller;

import journalLabb.dto.MessageDto;
import journalLabb.dto.SendMessageDto;
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
    public MessageDto sendMessage(
            @RequestBody SendMessageDto dto,
            Authentication authentication
    ) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return messageService.sendMessage(principal.getUserId(), dto);
    }

    @GetMapping("/my")
    public List<MessageDto> myMessages(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return messageService.getMessagesForUser(principal.getUserId());
    }

    @GetMapping("/patient/{patientId}")
    public List<MessageDto> getPatientMessages(@PathVariable Long patientId) {
        return messageService.getMessagesForPatient(patientId);
    }
}
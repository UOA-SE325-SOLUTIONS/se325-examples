package uoa.se325.parolees.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import uoa.se325.parolees.dto.ParoleeDTO;

@Service
public class MovementBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MovementBroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastMovementUpdate(ParoleeDTO movement) {
        messagingTemplate.convertAndSend("/topic/movements", movement);
    }
}

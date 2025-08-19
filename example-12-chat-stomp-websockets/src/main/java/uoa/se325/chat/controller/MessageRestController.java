package uoa.se325.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uoa.se325.chat.dto.NewMessage;
import uoa.se325.chat.model.Message;
import uoa.se325.chat.repository.MessageRepository;
import uoa.se325.chat.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageRestController {

    private final MessageRepository messageRepository;
    private final MessageService messageService;

    @Autowired
    public MessageRestController(MessageRepository messageRepository, MessageService messageService) {
        this.messageRepository = messageRepository;
        this.messageService = messageService;
    }

    /**
     * Retrieves all messages from the database
     * @return List of all messages ordered by timestamp
     */
    @GetMapping
    public List<Message> retrieveMessages() {
        return messageRepository.findAllOrdered();
    }

    /**
     * REST endpoint to broadcast a message to all connected WebSocket clients
     * @param newMessage the message to broadcast (sender and content)
     * @return the created message with id and timestamp
     */
    @PostMapping("/broadcast")
    public ResponseEntity<Message> broadcastMessage(@RequestBody NewMessage newMessage) {
        // Validate input
        if (newMessage.getSender() == null || newMessage.getSender().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (newMessage.getContent() == null || newMessage.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Use the service to create and broadcast the message
        Message message = messageService.createAndBroadcastMessage(newMessage);

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
}

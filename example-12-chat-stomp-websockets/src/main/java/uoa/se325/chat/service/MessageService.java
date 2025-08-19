package uoa.se325.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import uoa.se325.chat.dto.NewMessage;
import uoa.se325.chat.model.Message;
import uoa.se325.chat.repository.MessageRepository;

/**
 * Service class for handling message operations including saving and broadcasting
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessageService(MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Creates a new message, saves it to the database, and broadcasts it to all connected WebSocket clients
     * @param newMessage the message data (sender and content)
     * @return the created message with id and timestamp
     */
    public Message createAndBroadcastMessage(NewMessage newMessage) {
        // Create and save the message
        Message message = createMessage(newMessage);

        // Broadcast to all connected WebSocket clients
        messagingTemplate.convertAndSend("/topic/messages", message);

        return message;
    }

    /**
     * Creates a new message and saves it to the database without broadcasting
     * @param newMessage the message data (sender and content)
     * @return the created message with id and timestamp
     */
    public Message createMessage(NewMessage newMessage) {
        Message message = new Message(newMessage.getSender(), newMessage.getContent());
        messageRepository.save(message);
        return message;
    }
}

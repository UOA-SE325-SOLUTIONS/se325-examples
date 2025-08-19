package uoa.se325.chat.controller.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import uoa.se325.chat.dto.NewMessage;
import uoa.se325.chat.model.Message;
import uoa.se325.chat.service.MessageService;

@Controller
public class MessageWebSocketController {

    private final MessageService messageService;

    @Autowired
    public MessageWebSocketController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Receives a {@link NewMessage} in JSON format with a "sender" and "content" property.
     * <p>
     * Uses the MessageService to save the message to the database and broadcast it.
     * The @SendTo annotation is still used for the return value to maintain WebSocket behavior.
     *
     * @param incoming the sender and content
     * @return a newly created {@link Message} to broadcast to /topic/messages
     */
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(NewMessage incoming) {
        // Use the service to create the message (which also saves it)
        // Note: We still return the message for @SendTo to work, but the service
        // also broadcasts it via SimpMessagingTemplate for REST consistency
        return messageService.createMessage(incoming);
    }
}

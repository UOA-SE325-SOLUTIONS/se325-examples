package uoa.se325.example09jpaspringboot;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uoa.se325.example09jpaspringboot.model.Comment;
import uoa.se325.example09jpaspringboot.model.Message;
import uoa.se325.example09jpaspringboot.repository.MessageRepository;

import java.time.LocalDateTime;

@Component
public class Example09CommandLineRunner implements CommandLineRunner {

    private final MessageRepository messageRepository;

    @Autowired
    public Example09CommandLineRunner(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
//    @Transactional
    public void run(String... args) throws Exception {
        // Create a message and add some comments to it
        Message message = new Message("Hello, World!");
        message.getComments().add(new Comment("First comment", LocalDateTime.now()));
        message.getComments().add(new Comment("Second comment", LocalDateTime.now()));
        message.getComments().add(new Comment("Third comment", LocalDateTime.now()));

        // Save the message
        messageRepository.save(message);

        // Print the message - we will see that its id has been assigned
        System.out.println("Message saved with id:");
        System.out.println(message);

        // Get the message from the DB
        Message messageFromDB = messageRepository
                .findById(1L)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        System.out.println("Message from database:");
        System.out.println(messageFromDB);
        for (Comment c : messageFromDB.getComments()) {
            System.out.println("\t- Comment: " + c);
        }

        var list = messageRepository.findByCreationTimeBetween(LocalDateTime.parse("2021-01-01T00:00"), LocalDateTime.parse("2027-01-02T00:00"));
        System.out.println("Messages count: " + list.size());
        for (Message m : list) {
            System.out.println("Message: " + m);
        }
    }
}

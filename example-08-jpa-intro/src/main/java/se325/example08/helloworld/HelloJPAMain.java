package se325.example08.helloworld;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;
import java.util.List;

public class HelloJPAMain {

    public static void main(String[] args) {

        // Create entity manager using the definitions in resources/META-INF/persistence.xml
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("se325.example08.helloworld");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // Need to wrap write operations in a transaction
        entityManager.getTransaction().begin();

        // Create a message and add some comments to it
        Message message = new Message("Hello, World!");
        message.getComments().add(new Comment("First comment", LocalDateTime.now()));
        message.getComments().add(new Comment("Second comment", LocalDateTime.now()));
        message.getComments().add(new Comment("Third comment", LocalDateTime.now()));

        // Persist the message (comments will automatically be persisted)
        entityManager.persist(message);
        entityManager.getTransaction().commit();

        // Grab all messages from the database - there should be just the one (from above).
        // Don't explicitly need a transaction for read operations with auto-commit mode active
        List<Message> messages = entityManager
                .createQuery("select m from Message m", Message.class)
                .getResultList();

        for (Message m : messages) {
            System.out.println("Message: " + m);
            for (Comment c : m.getComments()) {
                System.out.println("- Comment: " + c);
            }
        }

        // Modify the message content
        entityManager.getTransaction().begin();
        messages.get(0).setContent("Take me to your leader!");
        entityManager.persist(messages.get(0));
        entityManager.getTransaction().commit();
        entityManager.close();

        System.out.println("Done!!");
    }

}
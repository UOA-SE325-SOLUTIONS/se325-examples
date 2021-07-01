package se325.example09.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class HelloJPAMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloJPAMain.class);

    private static String QUERY = "select m from Message m";

    public static void main(String[] args) {

        // Create entity manager using the definitions in resources/META-INF/persistence.xml
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("se325.example09.helloworld");

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        // Create a message and add some comments to it
        Message message = new Message("Hello, World!");
        message.getComments().add(new Comment("First comment", LocalDateTime.now()));
        message.getComments().add(new Comment("Second comment", LocalDateTime.now()));
        message.getComments().add(new Comment("Third comment", LocalDateTime.now()));

        // Persist the message (comments will automatically be persisted)
        entityManager.persist(message);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();

        // Grab all messages from the database - there should be just the one (from above).
        List<Message> messages = entityManager.createQuery(QUERY, Message.class).getResultList();
        for (Message m : messages) {
            LOGGER.info("Message: " + m);
            for (Comment c : m.getComments()) {
                LOGGER.info("- Comment: " + c);
            }
        }

        // Modify the message content
        messages.get(0).setContent("Take me to your leader!");
        entityManager.persist(messages.get(0));
        entityManager.getTransaction().commit();
        entityManager.close();

        LOGGER.info("Done!!");
    }

}
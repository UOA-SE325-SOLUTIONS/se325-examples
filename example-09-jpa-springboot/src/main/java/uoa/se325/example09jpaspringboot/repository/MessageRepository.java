package uoa.se325.example09jpaspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uoa.se325.example09jpaspringboot.model.Message;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByContentContainingIgnoreCase(String content);

    List<Message> findByCreationTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}

package journalLabb.repository;

import journalLabb.model.Message;
import journalLabb.model.MessageThreadType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByReceiverId(Long receiverId);

    List<Message> findByPatientId(Long patientId);

    List<Message> findByPatientIdAndThreadTypeOrderByTimestampAsc(Long patientId, MessageThreadType threadType);
    List<Message> findBySenderIdOrReceiverIdOrderByTimestampAsc(Long senderId, Long receiverId);
}

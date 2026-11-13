package com.alertavia.repository;

import com.alertavia.model.Notification;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByCitizenIdOrderBySentAtDesc(String citizenId);

    long countByCitizenIdAndReadFalse(String citizenId);
}

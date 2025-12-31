package com.seyitkarahan.akilli_ajanda_api.repository;

import com.seyitkarahan.akilli_ajanda_api.entity.EventNotification;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventNotificationRepository extends JpaRepository<EventNotification, Long> {

    List<EventNotification> findByUser(User user);
}

package com.seyitkarahan.akilli_ajanda_api.repository;

import com.seyitkarahan.akilli_ajanda_api.entity.TaskNotification;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskNotificationRepository extends JpaRepository<TaskNotification, Long> {

    List<TaskNotification> findByUser(User user);
}
package com.seyitkarahan.akilli_ajanda_api.repository;

import com.seyitkarahan.akilli_ajanda_api.entity.Task;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUser(User user);

    List<Task> findByUserAndStatus(User user, com.seyitkarahan.akilli_ajanda_api.enums.TaskStatus status);
}

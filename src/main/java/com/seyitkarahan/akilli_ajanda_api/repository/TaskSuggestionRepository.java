package com.seyitkarahan.akilli_ajanda_api.repository;

import com.seyitkarahan.akilli_ajanda_api.entity.TaskSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskSuggestionRepository extends JpaRepository<TaskSuggestion, Long> {

    List<TaskSuggestion> findByUserIdAndIsAcceptedFalse(Long userId);
}

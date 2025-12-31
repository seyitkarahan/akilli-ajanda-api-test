package com.seyitkarahan.akilli_ajanda_api.repository;

import com.seyitkarahan.akilli_ajanda_api.entity.ImageFile;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

    List<ImageFile> findByUser(User user);

    List<ImageFile> findByTaskId(Long taskId);

    List<ImageFile> findByEventId(Long eventId);
}
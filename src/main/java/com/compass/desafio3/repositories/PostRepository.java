package com.compass.desafio3.repositories;

import com.compass.desafio3.enums.PostStatus;
import com.compass.desafio3.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsById(Long id);
    List<Post> findByStatus(PostStatus status);

    Optional<Post> findByIdAndStatus(Long postId, PostStatus postStatus);
}


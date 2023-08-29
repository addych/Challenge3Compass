package com.compass.desafio3.repositories;

import com.compass.desafio3.model.History;
import com.compass.desafio3.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByPostOrderByDateDesc(Post post);
}


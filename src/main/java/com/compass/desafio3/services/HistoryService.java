package com.compass.desafio3.services;

import com.compass.desafio3.enums.PostStatus;
import com.compass.desafio3.model.History;
import com.compass.desafio3.model.Post;
import com.compass.desafio3.repositories.HistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void addHistory(Post post, PostStatus status) {
        History history = new History();
        history.setPost(post);
        history.setDate(LocalDateTime.now());
        history.setStatus(status);
        historyRepository.save(history);
    }

    public List<History> getHistoryByPost(Post post) {
        return historyRepository.findByPostOrderByDateDesc(post);
    }

}


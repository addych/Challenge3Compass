package com.compass.desafio3.services;

import com.compass.desafio3.model.Comment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommentService {

    private final RestTemplate restTemplate;

    public CommentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Comment[] getCommentsForPost(Long postId) {
        String url = "https://jsonplaceholder.typicode.com/posts/" + postId + "/comments";
        return restTemplate.getForObject(url, Comment[].class);
    }
}


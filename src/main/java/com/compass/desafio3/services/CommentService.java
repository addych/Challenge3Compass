package com.compass.desafio3.services;

import com.compass.desafio3.enums.PostStatus;
import com.compass.desafio3.model.Comment;
import com.compass.desafio3.model.Post;
import com.compass.desafio3.repositories.CommentRepository;
import com.compass.desafio3.repositories.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CommentService {

    private final RestTemplate restTemplate;
    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    public CommentService(RestTemplate restTemplate, CommentRepository commentRepository, PostRepository postRepository) {
        this.restTemplate = restTemplate;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public ResponseEntity<String> fetchComments(Long postId) {
        Optional<Post> optionalPost = postRepository.findByIdAndStatus(postId, PostStatus.ENABLED);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.badRequest().body("No enabled post found with ID " + postId + ".");
        }

        String url = "https://jsonplaceholder.typicode.com/posts/" + postId + "/comments";
        Comment[] comments = restTemplate.getForObject(url, Comment[].class);

        for (Comment comment : comments) {
            comment.setPost(optionalPost.get());
            commentRepository.save(comment);
        }

        return ResponseEntity.ok("Comments fetched and stored successfully.");
    }





    public List<Comment> getCommentsForPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

}


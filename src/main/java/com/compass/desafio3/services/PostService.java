package com.compass.desafio3.services;

import com.compass.desafio3.enums.PostStatus;
import com.compass.desafio3.exceptions.InvalidPostIdException;
import com.compass.desafio3.exceptions.InvalidPostStatusException;
import com.compass.desafio3.exceptions.PostAlreadyExistsException;
import com.compass.desafio3.exceptions.PostNotFoundException;
import com.compass.desafio3.model.Comment;
import com.compass.desafio3.model.History;
import com.compass.desafio3.model.Post;
import com.compass.desafio3.repositories.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final RestTemplate restTemplate;
    private final PostRepository postRepository;
    private final HistoryService historyService;
    private final CommentService commentService;

    public PostService(RestTemplate restTemplate, PostRepository postRepository, HistoryService historyService, CommentService commentService) {
        this.restTemplate = restTemplate;
        this.postRepository = postRepository;
        this.historyService = historyService;
        this.commentService = commentService;
    }

    public ResponseEntity<String> processPost (Long postId) {
        try {
            validatePostId(postId);

            if (postRepository.existsById(postId)) {
                throw new PostAlreadyExistsException("Post with ID " + postId + " already exists.");
            }

            Post post = fetchPostById(postId);
            if (post == null) {
                throw new InvalidPostIdException("Invalid post ID.");
            }

            post.setStatus(PostStatus.ENABLED);
            postRepository.save(post);
            historyService.addHistory(post, PostStatus.ENABLED);
            return ResponseEntity.ok("Post processed and saved successfully.");
        } catch (InvalidPostIdException | PostAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<String> disablePost(Long postId) {
        try {
            validatePostId(postId);

            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isEmpty()) {
                throw new PostNotFoundException("No post found with ID " + postId + ".");
            }

            Post post = optionalPost.get();
            if (post.getStatus() != PostStatus.ENABLED) {
                throw new InvalidPostStatusException("Post with ID " + postId + " is not in ENABLED state.");
            }

            post.setStatus(PostStatus.DISABLED);
            postRepository.save(post);
            historyService.addHistory(post, PostStatus.DISABLED);
            return ResponseEntity.ok("Post with ID " + postId + " disabled successfully.");
        } catch (InvalidPostIdException | PostNotFoundException | InvalidPostStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<String> reprocessPost(Long postId) {
        try {
            validatePostId(postId);

            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isEmpty()) {
                throw new PostNotFoundException("No post found with ID " + postId + ".");
            }

            Post post = optionalPost.get();
            if (post.getStatus() != PostStatus.ENABLED && post.getStatus() != PostStatus.DISABLED) {
                throw new InvalidPostStatusException("Post status is invalid.");
            }

            PostStatus newStatus = (post.getStatus() == PostStatus.ENABLED) ? PostStatus.DISABLED : PostStatus.ENABLED;
            post.setStatus(newStatus);
            postRepository.save(post);
            historyService.addHistory(post, newStatus);
            return ResponseEntity.ok("Post reprocessed successfully.");
        } catch (InvalidPostIdException | PostNotFoundException | InvalidPostStatusException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    public Post fetchPostById(Long postId) {
        String url = "https://jsonplaceholder.typicode.com/posts/" + postId;
        return restTemplate.getForObject(url, Post.class);
    }

    public List<Post> getEnabledPosts() {
        List<Post> enabledPosts = postRepository.findByStatus(PostStatus.ENABLED);
        for (Post post : enabledPosts) {
            List<History> history = historyService.getHistoryByPost(post);
            post.setHistory(history);
            List<Comment> comments = commentService.getCommentsForPost(post.getId());
            post.setComments(comments);
        }
        return enabledPosts;
    }

    private void validatePostId(Long postId) {
        if (postId < 1 || postId > 100) {
            throw new InvalidPostIdException("Invalid post ID.");
        }
    }
}


package com.compass.desafio3.controller;

import com.compass.desafio3.enums.PostStatus;
import com.compass.desafio3.model.Post;
import com.compass.desafio3.repositories.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.compass.desafio3.services.JsonPlaceholderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final JsonPlaceholderService jsonPlaceholderService;
    private final PostRepository postRepository;

    public PostController(JsonPlaceholderService jsonPlaceholderService, PostRepository postRepository) {
        this.jsonPlaceholderService = jsonPlaceholderService;
        this.postRepository = postRepository;
    }

    @PostMapping("/{postId}")
    public void processPost(@PathVariable Long postId) {
        Post post = jsonPlaceholderService.getPostById(postId);

        if (post != null && postId >= 1 && postId <= 100 && !postRepository.existsById(postId)) {
            post.setStatus(PostStatus.ENABLED);
            postRepository.save(post);
            jsonPlaceholderService.addHistory(post, PostStatus.ENABLED);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> disablePost(@PathVariable Long postId) {
        if (postId >= 1 && postId <= 100) {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();
                if (post.getStatus() == PostStatus.ENABLED) {
                    post.setStatus(PostStatus.DISABLED);
                    postRepository.save(post);
                    jsonPlaceholderService.addHistory(post, PostStatus.DISABLED);
                    return ResponseEntity.ok("Post disabled successfully.");
                } else {
                    return ResponseEntity.badRequest().body("Post is not in ENABLED state.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid postId.");
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> reprocessPost(@PathVariable Long postId) {
        if (postId >= 1 && postId <= 100) {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();
                if (post.getStatus() == PostStatus.ENABLED || post.getStatus() == PostStatus.DISABLED) {
                    post.setStatus(post.getStatus() == PostStatus.ENABLED ? PostStatus.DISABLED : PostStatus.ENABLED);
                    postRepository.save(post);
                    jsonPlaceholderService.addHistory(post, post.getStatus());
                    return ResponseEntity.ok("Post reprocessed successfully.");
                } else {
                    return ResponseEntity.badRequest().body("Post status is invalid.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid postId.");
        }
    }

    @GetMapping
    public List<Post> getPosts() {
        return jsonPlaceholderService.getEnabledPosts();
    }
}

package com.compass.desafio3.controller;

import com.compass.desafio3.model.Post;
import com.compass.desafio3.services.CommentService;
import com.compass.desafio3.services.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;


    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<String> processPost(@PathVariable Long postId) {
        return postService.processPost(postId);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> disablePost(@PathVariable Long postId) {
        return postService.disablePost(postId);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> reprocessPost(@PathVariable Long postId) {
        return postService.reprocessPost(postId);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<String> getPostComments(@PathVariable Long postId) {
        return commentService.fetchComments(postId);
    }

    @GetMapping
    public List<Post> getPosts() {
        return postService.getEnabledPosts();
    }
}

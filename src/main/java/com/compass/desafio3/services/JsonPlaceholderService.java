package com.compass.desafio3.services;

import com.compass.desafio3.enums.PostStatus;
import com.compass.desafio3.model.Comment;
import com.compass.desafio3.model.History;
import com.compass.desafio3.model.Post;
import com.compass.desafio3.repositories.PostRepository;
import com.compass.desafio3.repositories.HistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class JsonPlaceholderService {

    private static final String API_URL = "https://jsonplaceholder.typicode.com";

    private final RestTemplate restTemplate;

    private final PostRepository postRepository;

    private final HistoryRepository historyRepository;



    public JsonPlaceholderService(RestTemplate restTemplate, PostRepository postRepository, HistoryRepository historyRepository) {
        this.restTemplate = restTemplate;
        this.postRepository = postRepository;
        this.historyRepository = historyRepository;
    }

    public void processAndSavePost(Long postId) {
        if (postId >= 1 && postId <= 100 && !postRepository.existsById(postId)) {
            String url = API_URL + "/posts/" + postId;
            Post post = restTemplate.getForObject(url, Post.class);

            if (post != null) {
                post.setStatus(PostStatus.ENABLED);
                postRepository.save(post);
                addHistory(post, PostStatus.ENABLED); // Add history
            }
        }
    }

    public void disablePost(Long postId) {
        Optional<Post> optionalPost = postRepository.findByIdAndStatus(postId, PostStatus.ENABLED);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setStatus(PostStatus.DISABLED);
            postRepository.save(post);
            addHistory(post, PostStatus.DISABLED); // Add history
        }
    }

    public void reprocessPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            PostStatus newStatus = (post.getStatus() == PostStatus.ENABLED) ? PostStatus.DISABLED : PostStatus.ENABLED;
            post.setStatus(newStatus);
            postRepository.save(post);
            addHistory(post, newStatus); // Add history
        }
    }

    public Post getPostById(Long postId) {
        String url = API_URL + "/posts/" + postId;
        return restTemplate.getForObject(url, Post.class);
    }

    public List<Post> getEnabledPosts() {
        List<Post> enabledPosts = postRepository.findByStatus(PostStatus.ENABLED);
        for (Post post : enabledPosts) {
            List<History> history = historyRepository.findByPostOrderByDateDesc(post);
            post.setHistory(history);
        }
        return enabledPosts;
    }

    private List<Comment> getCommentsForPost(Long postId) {
        String url = API_URL + "/posts/" + postId + "/comments";
        Comment[] comments = restTemplate.getForObject(url, Comment[].class);
        return Arrays.asList(comments);
    }

    public void updatePostStatus(Long postId, PostStatus newStatus) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (post.getStatus() != newStatus) {
                post.setStatus(newStatus);
                postRepository.save(post);

                History historyEntry = new History();
                historyEntry.setDate(LocalDateTime.now());
                historyEntry.setStatus(newStatus);
                historyEntry.setPost(post);
                historyRepository.save(historyEntry);
            }
        }
    }

    public void addHistory(Post post, PostStatus status) {
        History history = new History();
        history.setPost(post);
        history.setDate(LocalDateTime.now());
        history.setStatus(status);
        historyRepository.save(history);
    }
}

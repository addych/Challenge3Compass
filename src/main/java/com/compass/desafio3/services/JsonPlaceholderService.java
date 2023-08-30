package com.compass.desafio3.services;

import com.compass.desafio3.enums.PostStatus;
import com.compass.desafio3.model.Comment;
import com.compass.desafio3.model.History;
import com.compass.desafio3.model.Post;
import com.compass.desafio3.repositories.CommentRepository;
import com.compass.desafio3.repositories.PostRepository;
import com.compass.desafio3.repositories.HistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class JsonPlaceholderService {

    private static final String API_URL = "https://jsonplaceholder.typicode.com";

    private final RestTemplate restTemplate;

    private final PostRepository postRepository;

    private final HistoryRepository historyRepository;
    private final CommentRepository commentRepository;

    private final WebClient webClient;



    public JsonPlaceholderService(RestTemplate restTemplate, PostRepository postRepository, HistoryRepository historyRepository, CommentRepository commentRepository, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.postRepository = postRepository;
        this.historyRepository = historyRepository;
        this.commentRepository = commentRepository;
        this.webClient = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build();
    }

    public Mono<Post> fetchPostByIdAsync(Long postId) {
        return webClient.get()
                .uri("/posts/{postId}", postId)
                .retrieve()
                .bodyToMono(Post.class);
    }

    public List<Comment> fetchAndStoreComments(Long postId) {
        List<Comment> comments = webClient.get()
                .uri("/posts/{postId}/comments", postId)
                .retrieve()
                .bodyToFlux(Comment.class)
                .collectList()
                .block();

        if (comments != null) {
            for (Comment comment : comments) {
                comment.setPost(postRepository.findById(postId).get());
                commentRepository.save(comment);
            }
        }

        return comments;
    }

    public void processAndSavePost(Long postId) {
        if (postId >= 1 && postId <= 100 && !postRepository.existsById(postId)) {
            fetchPostByIdAsync(postId)
                    .subscribe(
                            post -> {
                                post.setStatus(PostStatus.ENABLED);
                                postRepository.save(post);
                                addHistory(post, PostStatus.ENABLED); // Add history
                            }
                    );
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
        for (Post post : enabledPosts) {
            List<Comment> comments = commentRepository.findByPostId(post.getId());
            post.setComments(comments);
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

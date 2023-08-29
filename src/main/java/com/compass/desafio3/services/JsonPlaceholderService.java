package com.compass.desafio3.services;

import com.compass.desafio3.enums.PostStatus;
import com.compass.desafio3.model.Comment;
import com.compass.desafio3.model.History;
import com.compass.desafio3.model.Post;
import com.compass.desafio3.repositories.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class JsonPlaceholderService {

    private static final String API_URL = "https://jsonplaceholder.typicode.com";

    private final RestTemplate restTemplate;

    private final PostRepository postRepository;



    public JsonPlaceholderService(RestTemplate restTemplate, PostRepository postRepository) {
        this.restTemplate = restTemplate;
        this.postRepository = postRepository;
    }

    public void processPost(Long postId) {
        if (postId >= 1 && postId <= 100 && !postRepository.existsById(postId)) {
            String url = API_URL + "/posts/" + postId;
            Post post = restTemplate.getForObject(url, Post.class);

            if (post != null) {
                post.setStatus(PostStatus.ENABLED); // Set status to enabled
                postRepository.save(post);
            }
        }
    }

    public Post getPostById(Long postId) {
        String url = API_URL + "/posts/" + postId;
        return restTemplate.getForObject(url, Post.class);
    }

    public List<Post> getEnabledPosts() {
        return postRepository.findByStatus(PostStatus.ENABLED);
    }

    private List<Comment> getCommentsForPost(Long postId) {
        String url = API_URL + "/posts/" + postId + "/comments";
        Comment[] comments = restTemplate.getForObject(url, Comment[].class);
        return Arrays.asList(comments);
    }

    private List<History> getHistoryForPost(Long postId) {
        // Implement logic to retrieve history entries for a post
        // You can use restTemplate to fetch the history from the API
        // and map it to HistoryEntry objects
        return new ArrayList<>();
    }
}

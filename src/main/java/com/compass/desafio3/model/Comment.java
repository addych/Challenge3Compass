package com.compass.desafio3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Comment {
    @Id
    private Long id;
    private String body;

    @ManyToOne
    private Post post;

    // Getters and setters
}

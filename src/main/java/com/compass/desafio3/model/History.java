package com.compass.desafio3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.Date;

@Entity
public class History {
    @Id
    private Long id;
    private Date date;
    private String status;

    @ManyToOne
    private Post post;

    // Getters and setters
}

package com.velocity.itest.avian.dto;

import java.time.LocalDateTime;

public class SightingDto {
    private Long id;
    private String location;
    private LocalDateTime dateTime;
    private BirdDto bird;

    // Default constructor
    public SightingDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public BirdDto getBird() {
        return bird;
    }

    public void setBird(BirdDto bird) {
        this.bird = bird;
    }
}
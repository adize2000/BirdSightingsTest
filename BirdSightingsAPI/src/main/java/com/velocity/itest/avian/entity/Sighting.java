package com.velocity.itest.avian.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sightings")
public class Sighting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bird_id")
    private Bird bird;

    private String location;
    private LocalDateTime dateTime;

    // Default constructor
    public Sighting() {
    }

    public Sighting(Bird bird, String location, LocalDateTime dateTime) {
        this.bird = bird;
        this.location = location;
        this.dateTime = dateTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bird getBird() {
        return bird;
    }

    public void setBird(Bird bird) {
        this.bird = bird;
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
}
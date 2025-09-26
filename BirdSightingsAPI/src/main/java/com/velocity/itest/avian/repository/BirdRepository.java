package com.velocity.itest.avian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.velocity.itest.avian.entity.Bird;

import java.util.List;

@Repository
public interface BirdRepository extends JpaRepository<Bird, Long> {
    
    // Find birds by name
    List<Bird> findByName(String name);
    
    // Find birds by name and color
    List<Bird> findByNameAndColor(String name, String color);
}
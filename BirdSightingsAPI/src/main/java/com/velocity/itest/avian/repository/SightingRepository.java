package com.velocity.itest.avian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.velocity.itest.avian.entity.Bird;
import com.velocity.itest.avian.entity.Sighting;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SightingRepository extends JpaRepository<Sighting, Long> {

    // Find sightings by bird, location, and a time interval
    List<Sighting> findByBirdAndLocationAndDateTimeBetween(Bird bird, String location, LocalDateTime startDate, LocalDateTime endDate);

    // Find sightings by bird
    List<Sighting> findByBird(Bird bird);
    
    // Find sightings by location
    List<Sighting> findByLocation(String location);
}

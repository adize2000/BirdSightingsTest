package com.velocity.itest.avian.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.velocity.itest.avian.dto.BirdDto;
import com.velocity.itest.avian.dto.SightingDto;
import com.velocity.itest.avian.entity.Bird;
import com.velocity.itest.avian.entity.Sighting;
import com.velocity.itest.avian.mapper.BirdMapper;
import com.velocity.itest.avian.mapper.SightingMapper;
import com.velocity.itest.avian.repository.BirdRepository;
import com.velocity.itest.avian.repository.SightingRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class BirdController {

    @Autowired
    private BirdRepository birdRepository;

    @Autowired
    private SightingRepository sightingRepository;
    
    @Autowired
    private BirdMapper birdMapper;
    
    @Autowired
    private SightingMapper sightingMapper;

    @PostConstruct
    public void initializeData() {
        // Create example birds
        Bird eagle = new Bird("Eagle", "Brown", 5.5, 75.0);
        birdRepository.save(eagle);

        Bird sparrow = new Bird("Sparrow", "Grey", 0.05, 15.0);
        birdRepository.save(sparrow);
        
        Bird robin = new Bird("Robin", "Red", 0.1, 20.0);
        birdRepository.save(robin);

        // Create example sightings for each bird
        sightingRepository.save(new Sighting(eagle, "Grand Canyon", LocalDateTime.now()));
        sightingRepository.save(new Sighting(eagle, "Rocky Mountains", LocalDateTime.now().minusDays(5)));

        sightingRepository.save(new Sighting(sparrow, "Backyard", LocalDateTime.now().minusHours(2)));
        sightingRepository.save(new Sighting(sparrow, "City Park", LocalDateTime.now().minusMonths(1)));
        
        sightingRepository.save(new Sighting(robin, "Central Park", LocalDateTime.now().minusDays(1)));
    }

    /**
     * Bird Endpoints
     */

    @GetMapping("/birds")
    public List<BirdDto> getAllBirds() {
        return birdRepository.findAll().stream()
                .map(birdMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/birds/{id}")
    public ResponseEntity<BirdDto> getBirdById(@PathVariable Long id) {
        Optional<Bird> bird = birdRepository.findById(id);
        return bird.map(value -> new ResponseEntity<>(birdMapper.toDto(value), HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/birds")
    public ResponseEntity<Bird> createBird(@RequestBody Bird bird) {
        try {
            Bird newBird = birdRepository.save(bird);
            return new ResponseEntity<>(newBird, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/birds/{id}")
    public ResponseEntity<Bird> updateBird(@PathVariable Long id, @RequestBody Bird birdDetails) {
        Optional<Bird> birdData = birdRepository.findById(id);
        if (birdData.isPresent()) {
            Bird bird = birdData.get();
            bird.setName(birdDetails.getName());
            bird.setColor(birdDetails.getColor());
            bird.setWeight(birdDetails.getWeight());
            bird.setHeight(birdDetails.getHeight());
            return new ResponseEntity<>(birdRepository.save(bird), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/birds/{id}")
    public ResponseEntity<HttpStatus> deleteBird(@PathVariable Long id) {
        try {
            birdRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/birds/query")
    public List<BirdDto> queryBirds(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) String color) {
        if (name != null && color != null) {
            return birdRepository.findByNameAndColor(name, color).stream()
                    .map(birdMapper::toDto)
                    .collect(Collectors.toList());
        } else if (name != null) {
            return birdRepository.findByName(name).stream()
                    .map(birdMapper::toDto)
                    .collect(Collectors.toList());
        }
        return birdRepository.findAll().stream()
                .map(birdMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Sighting Endpoints
     */

    @GetMapping("/sightings")
    public List<SightingDto> getAllSightings() {
        return sightingRepository.findAll().stream()
                .map(sightingMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @PostMapping("/sightings")
    public ResponseEntity<Sighting> createSighting(@RequestBody Sighting sighting) {
        try {
            sighting.setBird(birdRepository.findById(sighting.getBird().getId()).orElseThrow(() -> new RuntimeException("Bird not found")));
            Sighting newSighting = sightingRepository.save(sighting);
            return new ResponseEntity<>(newSighting, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/sightings/{id}")
    public ResponseEntity<HttpStatus> deleteSighting(@PathVariable Long id) {
        try {
            sightingRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/sightings/query")
    public List<SightingDto> querySightings(@RequestParam(required = false) String location,
                                         @RequestParam(required = false) Long birdId,
                                         @RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate) {
        
        Optional<Bird> bird = Optional.empty();
        if (birdId != null) {
            bird = birdRepository.findById(birdId);
        }

        if (bird.isPresent() && location != null && startDate != null && endDate != null) {
            return sightingRepository.findByBirdAndLocationAndDateTimeBetween(
                bird.get(), location, LocalDateTime.parse(startDate), LocalDateTime.parse(endDate)
            ).stream()
            .map(sightingMapper::toDto)
            .collect(Collectors.toList());
        } else if (bird.isPresent()) {
            return sightingRepository.findByBird(bird.get()).stream()
                .map(sightingMapper::toDto)
                .collect(Collectors.toList());
        } else if (location != null) {
            return sightingRepository.findByLocation(location).stream()
                .map(sightingMapper::toDto)
                .collect(Collectors.toList());
        }
        
        return sightingRepository.findAll().stream()
                .map(sightingMapper::toDto)
                .collect(Collectors.toList());
    }
}
package com.velocity.itest.avian.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.velocity.itest.avian.dto.SightingDto;
import com.velocity.itest.avian.entity.Sighting;

@Component
public class SightingMapper {

    @Autowired
    private BirdMapper birdMapper;

    public SightingDto toDto(Sighting sighting) {
        if (sighting == null) {
            return null;
        }
        SightingDto dto = new SightingDto();
        dto.setId(sighting.getId());
        dto.setLocation(sighting.getLocation());
        dto.setDateTime(sighting.getDateTime());
        
        // Map the associated Bird to its DTO
        if (sighting.getBird() != null) {
            dto.setBird(birdMapper.toDto(sighting.getBird()));
        }
        return dto;
    }
}

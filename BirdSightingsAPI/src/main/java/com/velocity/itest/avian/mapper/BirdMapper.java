package com.velocity.itest.avian.mapper;

import org.springframework.stereotype.Component;

import com.velocity.itest.avian.dto.BirdDto;
import com.velocity.itest.avian.entity.Bird;

@Component
public class BirdMapper {

    public BirdDto toDto(Bird bird) {
        if (bird == null) {
            return null;
        }
        BirdDto dto = new BirdDto();
        dto.setId(bird.getId());
        dto.setName(bird.getName());
        dto.setColor(bird.getColor());
        dto.setWeight(bird.getWeight());
        dto.setHeight(bird.getHeight());
        return dto;
    }
}
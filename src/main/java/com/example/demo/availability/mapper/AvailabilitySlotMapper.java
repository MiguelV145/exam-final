package com.example.demo.availability.mapper;

import com.example.demo.availability.dto.AvailabilitySlotCreateDto;
import com.example.demo.availability.dto.AvailabilitySlotResponseDto;
import com.example.demo.availability.dto.AvailabilitySlotUpdateDto;
import com.example.demo.availability.dto.DayOfWeekEs;
import com.example.demo.availability.entity.AvailabilitySlot;
import org.springframework.stereotype.Component;

@Component
public class AvailabilitySlotMapper {
    
    public AvailabilitySlot toEntity(AvailabilitySlotCreateDto dto) {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setDayOfWeek(dto.getDayOfWeek() != null ? dto.getDayOfWeek().toJava() : null);
        slot.setStartTime(dto.getStartTime());
        slot.setEndTime(dto.getEndTime());
        slot.setModality(dto.getModality());
        slot.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
        return slot;
    }
    
    public AvailabilitySlotResponseDto toResponseDto(AvailabilitySlot slot) {
        AvailabilitySlotResponseDto dto = new AvailabilitySlotResponseDto();
        dto.setId(slot.getId());
        dto.setProgrammerId(slot.getProgrammer().getId());
        dto.setProgrammerUsername(slot.getProgrammer().getUsername());
        dto.setDayOfWeek(DayOfWeekEs.fromJava(slot.getDayOfWeek()));
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setModality(slot.getModality());
        dto.setEnabled(slot.isEnabled());
        dto.setCreatedAt(slot.getCreatedAt());
        dto.setUpdatedAt(slot.getUpdatedAt());
        return dto;
    }
    
    public void updateEntityFromDto(AvailabilitySlot slot, AvailabilitySlotUpdateDto dto) {
        if (dto.getDayOfWeek() != null) {
            slot.setDayOfWeek(dto.getDayOfWeek().toJava());
        }
        if (dto.getStartTime() != null) {
            slot.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            slot.setEndTime(dto.getEndTime());
        }
        if (dto.getModality() != null) {
            slot.setModality(dto.getModality());
        }
        if (dto.getEnabled() != null) {
            slot.setEnabled(dto.getEnabled());
        }
    }
}

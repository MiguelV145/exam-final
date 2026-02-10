package com.example.demo.availability.service;

import com.example.demo.availability.dto.AvailabilitySlotCreateDto;
import com.example.demo.availability.dto.AvailabilitySlotResponseDto;
import com.example.demo.availability.dto.AvailabilitySlotUpdateDto;
import com.example.demo.users.entity.User;

import java.util.List;

public interface AvailabilitySlotService {
    
    AvailabilitySlotResponseDto createSlot(AvailabilitySlotCreateDto dto, User currentUser);
    
    AvailabilitySlotResponseDto updateSlot(Long slotId, AvailabilitySlotUpdateDto dto, User currentUser);
    
    void deleteSlot(Long slotId, User currentUser);
    
    List<AvailabilitySlotResponseDto> getMySlots(User currentUser);
    
    List<AvailabilitySlotResponseDto> getSlotsByProgrammer(Long programmerId);
}

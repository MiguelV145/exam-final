package com.example.demo.availability.service;

import com.example.demo.availability.dto.AvailabilitySlotCreateDto;
import com.example.demo.availability.dto.AvailabilitySlotResponseDto;
import com.example.demo.availability.dto.AvailabilitySlotUpdateDto;
import com.example.demo.availability.entity.AvailabilitySlot;
import com.example.demo.availability.mapper.AvailabilitySlotMapper;
import com.example.demo.availability.repository.AvailabilitySlotRepository;
import com.example.demo.roles.entity.RoleName;
import com.example.demo.shared.exception.ResourceNotFoundException;
import com.example.demo.users.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilitySlotServiceImpl implements AvailabilitySlotService {
    
    private final AvailabilitySlotRepository slotRepository;
    private final AvailabilitySlotMapper slotMapper;
    
    public AvailabilitySlotServiceImpl(AvailabilitySlotRepository slotRepository, 
                                       AvailabilitySlotMapper slotMapper) {
        this.slotRepository = slotRepository;
        this.slotMapper = slotMapper;
    }
    
    @Override
    @Transactional
    public AvailabilitySlotResponseDto createSlot(AvailabilitySlotCreateDto dto, User currentUser) {
        // Validar que el usuario es PROGRAMADOR
        validateProgrammerRole(currentUser);
        
        // Validar horarios
        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }
        
        // Validar solapamientos
        List<AvailabilitySlot> overlapping = slotRepository.findOverlappingSlots(
            currentUser.getId(),
            dto.getDayOfWeek().toJava(),
            dto.getStartTime(),
            dto.getEndTime()
        );
        
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("El horario se superpone con un slot de disponibilidad existente");
        }
        
        AvailabilitySlot slot = slotMapper.toEntity(dto);
        slot.setProgrammer(currentUser);
        
        slot = slotRepository.save(slot);
        return slotMapper.toResponseDto(slot);
    }
    
    @Override
    @Transactional
    public AvailabilitySlotResponseDto updateSlot(Long slotId, AvailabilitySlotUpdateDto dto, User currentUser) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new ResourceNotFoundException("Slot de disponibilidad no encontrado con id: " + slotId));
        
        // Validar ownership (solo el dueño o ADMIN)
        validateOwnership(slot, currentUser);
        
        // Actualizar campos
        slotMapper.updateEntityFromDto(slot, dto);
        
        // Validar horarios si cambiaron
        if (slot.getEndTime() != null && slot.getStartTime() != null && 
            !slot.getEndTime().isAfter(slot.getStartTime())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }
        
        // Validar solapamientos si cambiaron datos relevantes
        if (dto.getDayOfWeek() != null || dto.getStartTime() != null || dto.getEndTime() != null) {
            List<AvailabilitySlot> overlapping = slotRepository.findOverlappingSlots(
                slot.getProgrammer().getId(),
                slot.getDayOfWeek(),
                slot.getStartTime(),
                slot.getEndTime()
            );
            
            // Excluir el slot actual de la validación
            overlapping = overlapping.stream()
                .filter(s -> !s.getId().equals(slotId))
                .collect(Collectors.toList());
            
            if (!overlapping.isEmpty()) {
                throw new IllegalArgumentException("El horario se superpone con un slot de disponibilidad existente");
            }
        }
        
        slot = slotRepository.save(slot);
        return slotMapper.toResponseDto(slot);
    }
    
    @Override
    @Transactional
    public void deleteSlot(Long slotId, User currentUser) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new ResourceNotFoundException("Slot de disponibilidad no encontrado con id: " + slotId));
        
        // Validar ownership (solo el dueño o ADMIN)
        validateOwnership(slot, currentUser);
        
        slotRepository.delete(slot);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AvailabilitySlotResponseDto> getMySlots(User currentUser) {
        validateProgrammerRole(currentUser);
        
        List<AvailabilitySlot> slots = slotRepository.findByProgrammerId(currentUser.getId());
        return slots.stream()
            .map(slotMapper::toResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AvailabilitySlotResponseDto> getSlotsByProgrammer(Long programmerId) {
        List<AvailabilitySlot> slots = slotRepository.findByProgrammerIdAndEnabledTrue(programmerId);
        return slots.stream()
            .map(slotMapper::toResponseDto)
            .collect(Collectors.toList());
    }
    
    private void validateProgrammerRole(User user) {
        boolean isProgrammer = user.getRoles().stream()
            .anyMatch(role -> role.getName() == RoleName.PROGRAMADOR);
        
        if (!isProgrammer) {
            throw new AccessDeniedException("Solo usuarios con rol PROGRAMADOR pueden gestionar slots de disponibilidad");
        }
    }
    
    private void validateOwnership(AvailabilitySlot slot, User currentUser) {
        boolean isAdmin = currentUser.getRoles().stream()
            .anyMatch(role -> role.getName() == RoleName.ADMIN);
        
        boolean isOwner = slot.getProgrammer().getId().equals(currentUser.getId());
        
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("No tienes permiso para modificar este slot de disponibilidad");
        }
    }
}

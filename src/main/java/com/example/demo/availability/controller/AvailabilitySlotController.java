package com.example.demo.availability.controller;

import com.example.demo.availability.dto.AvailabilitySlotCreateDto;
import com.example.demo.availability.dto.AvailabilitySlotResponseDto;
import com.example.demo.availability.dto.AvailabilitySlotUpdateDto;
import com.example.demo.availability.service.AvailabilitySlotService;
import com.example.demo.users.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
@Tag(name = "Availability", description = "Programmer availability management endpoints")
public class AvailabilitySlotController {
    
    private final AvailabilitySlotService slotService;
    
    public AvailabilitySlotController(AvailabilitySlotService slotService) {
        this.slotService = slotService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('PROGRAMADOR')")
    @Operation(summary = "Create availability slot", description = "Create a new availability slot for the current programmer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Slot created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or overlapping slot"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public ResponseEntity<AvailabilitySlotResponseDto> createSlot(
            @Valid @RequestBody AvailabilitySlotCreateDto dto,
            @AuthenticationPrincipal User currentUser) {
        AvailabilitySlotResponseDto response = slotService.createSlot(dto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasRole('PROGRAMADOR')")
    @Operation(summary = "Get my availability slots", description = "Get all availability slots for the current programmer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Slots retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public ResponseEntity<List<AvailabilitySlotResponseDto>> getMySlots(
            @AuthenticationPrincipal User currentUser) {
        List<AvailabilitySlotResponseDto> slots = slotService.getMySlots(currentUser);
        return ResponseEntity.ok(slots);
    }
    
    @GetMapping("/programmer/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get programmer availability", description = "Get all enabled availability slots for a specific programmer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Slots retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<List<AvailabilitySlotResponseDto>> getSlotsByProgrammer(
            @PathVariable Long id) {
        List<AvailabilitySlotResponseDto> slots = slotService.getSlotsByProgrammer(id);
        return ResponseEntity.ok(slots);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROGRAMADOR', 'ADMIN')")
    @Operation(summary = "Update availability slot", description = "Update an existing availability slot (owner or admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Slot updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or overlapping slot"),
        @ApiResponse(responseCode = "403", description = "Not authorized"),
        @ApiResponse(responseCode = "404", description = "Slot not found")
    })
    public ResponseEntity<AvailabilitySlotResponseDto> updateSlot(
            @PathVariable Long id,
            @RequestBody AvailabilitySlotUpdateDto dto,
            @AuthenticationPrincipal User currentUser) {
        AvailabilitySlotResponseDto response = slotService.updateSlot(id, dto, currentUser);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROGRAMADOR', 'ADMIN')")
    @Operation(summary = "Delete availability slot", description = "Delete an existing availability slot (owner or admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Slot deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized"),
        @ApiResponse(responseCode = "404", description = "Slot not found")
    })
    public ResponseEntity<Void> deleteSlot(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        slotService.deleteSlot(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}

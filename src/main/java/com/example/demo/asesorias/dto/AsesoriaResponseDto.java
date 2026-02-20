package com.example.demo.asesorias.dto;

import com.example.demo.availability.entity.Modality;
import com.example.demo.asesorias.entity.AsesoriaStatus;
import java.time.LocalDateTime;

public record AsesoriaResponseDto(
    Long id,
    LocalDateTime startAt,
    Integer durationMinutes,
    Modality modality,
    AsesoriaStatus status,
    String topic,
    String notes,
    String responseMsg,
    ProgrammerSummaryDto programmer,
    ClientSummaryDto client
) {
    public record ProgrammerSummaryDto(
        Long id,
        String username,
        String email,
        String displayName
    ) {
    }

    public record ClientSummaryDto(
        Long id,
        String username,
        String email
    ) {
    }
}

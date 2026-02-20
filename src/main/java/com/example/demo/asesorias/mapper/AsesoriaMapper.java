package com.example.demo.asesorias.mapper;

import com.example.demo.asesorias.dto.CreateAsesoriaDto;
import com.example.demo.asesorias.dto.AsesoriaResponseDto;
import com.example.demo.asesorias.dto.UpdateAsesoriaStatusDto;
import com.example.demo.asesorias.entity.Asesoria;
import com.example.demo.users.entity.User;

public final class AsesoriaMapper {
    private AsesoriaMapper() {
    }

    public static Asesoria toEntity(CreateAsesoriaDto request) {
        Asesoria asesoria = new Asesoria();
        asesoria.setStartAt(request.startAt());
        asesoria.setDurationMinutes(request.durationMinutes());
        asesoria.setModality(request.modality());
        asesoria.setTopic(request.topic());
        asesoria.setNotes(request.notes());
        return asesoria;
    }

    public static void updateStatus(UpdateAsesoriaStatusDto request, Asesoria asesoria) {
        if (request.status() != null) {
            asesoria.setStatus(request.status());
        }
        if (request.responseMsg() != null) {
            asesoria.setResponseMsg(request.responseMsg());
        }
    }

    public static AsesoriaResponseDto toResponse(Asesoria asesoria) {
        AsesoriaResponseDto.ProgrammerSummaryDto programmer = toProgrammerSummary(asesoria.getProgrammer());
        AsesoriaResponseDto.ClientSummaryDto client = toClientSummary(asesoria.getClient());
        return new AsesoriaResponseDto(
            asesoria.getId(),
            asesoria.getStartAt(),
            asesoria.getDurationMinutes(),
            asesoria.getModality(),
            asesoria.getStatus(),
            asesoria.getTopic(),
            asesoria.getNotes(),
            asesoria.getResponseMsg(),
            programmer,
            client
        );
    }

    private static AsesoriaResponseDto.ProgrammerSummaryDto toProgrammerSummary(User programmer) {
        if (programmer == null) {
            return null;
        }

        String displayName = programmer.getProfile() != null ? programmer.getProfile().getDisplayName() : null;
        return new AsesoriaResponseDto.ProgrammerSummaryDto(
            programmer.getId(),
            programmer.getUsername(),
            programmer.getEmail(),
            displayName
        );
    }

    private static AsesoriaResponseDto.ClientSummaryDto toClientSummary(User client) {
        if (client == null) {
            return null;
        }

        return new AsesoriaResponseDto.ClientSummaryDto(
            client.getId(),
            client.getUsername(),
            client.getEmail()
        );
    }
}


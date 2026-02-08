package com.example.demo.asesorias.mapper;

import com.example.demo.asesorias.dto.CreateAsesoriaDto;
import com.example.demo.asesorias.dto.AsesoriaResponseDto;
import com.example.demo.asesorias.dto.UpdateAsesoriaStatusDto;
import com.example.demo.asesorias.entity.Asesoria;

public final class AsesoriaMapper {
    private AsesoriaMapper() {
    }

    public static Asesoria toEntity(CreateAsesoriaDto request) {
        Asesoria asesoria = new Asesoria();
        asesoria.setDate(request.date());
        asesoria.setTime(request.time());
        asesoria.setComment(request.comment());
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
        Long programmerId = asesoria.getProgrammer() == null ? null : asesoria.getProgrammer().getId();
        Long clientId = asesoria.getClient() == null ? null : asesoria.getClient().getId();
        return new AsesoriaResponseDto(
            asesoria.getId(),
            asesoria.getDate(),
            asesoria.getTime(),
            asesoria.getComment(),
            asesoria.getStatus(),
            asesoria.getResponseMsg(),
            programmerId,
            clientId
        );
    }
}


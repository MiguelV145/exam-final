package com.example.demo.asesorias.service;

import com.example.demo.asesorias.dto.CreateAsesoriaDto;
import com.example.demo.asesorias.dto.AsesoriaResponseDto;
import com.example.demo.asesorias.dto.AsesoriaMyRequestsResponseDto;
import com.example.demo.asesorias.dto.UpdateAsesoriaDto;
import com.example.demo.asesorias.dto.UpdateAsesoriaStatusDto;
import com.example.demo.asesorias.dto.CancelAsesoriaDto;
import java.util.List;

public interface AsesoriaService {
    List<AsesoriaResponseDto> listAll();
    AsesoriaMyRequestsResponseDto getMyRequests();
    List<AsesoriaResponseDto> listByProgrammer(Long programmerId);
    List<AsesoriaResponseDto> listByClient(Long clientId);
    AsesoriaResponseDto getById(Long id);
    AsesoriaResponseDto create(CreateAsesoriaDto request);
    AsesoriaResponseDto update(Long id, UpdateAsesoriaDto request);
    AsesoriaResponseDto updateStatus(Long id, UpdateAsesoriaStatusDto request);
    AsesoriaResponseDto cancel(Long id, CancelAsesoriaDto request);
    void delete(Long id);
}

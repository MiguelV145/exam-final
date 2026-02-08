package com.example.demo.asesorias.service;

import com.example.demo.asesorias.dto.CreateAsesoriaDto;
import com.example.demo.asesorias.dto.AsesoriaResponseDto;
import com.example.demo.asesorias.dto.UpdateAsesoriaStatusDto;
import java.util.List;

public interface AsesoriaService {
    List<AsesoriaResponseDto> listAll();
    List<AsesoriaResponseDto> listByProgrammer(Long programmerId);
    List<AsesoriaResponseDto> listByClient(Long clientId);
    AsesoriaResponseDto getById(Long id);
    AsesoriaResponseDto create(CreateAsesoriaDto request);
    AsesoriaResponseDto updateStatus(Long id, UpdateAsesoriaStatusDto request);
    void delete(Long id);
}

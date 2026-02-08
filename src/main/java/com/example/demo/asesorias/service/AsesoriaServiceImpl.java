package com.example.demo.asesorias.service;

import com.example.demo.asesorias.dto.CreateAsesoriaDto;
import com.example.demo.asesorias.dto.AsesoriaResponseDto;
import com.example.demo.asesorias.dto.UpdateAsesoriaStatusDto;
import com.example.demo.asesorias.entity.Asesoria;
import com.example.demo.asesorias.mapper.AsesoriaMapper;
import com.example.demo.asesorias.repository.AsesoriaRepository;
import com.example.demo.shared.exception.ResourceNotFoundException;
import com.example.demo.users.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AsesoriaServiceImpl implements AsesoriaService {
    private final AsesoriaRepository asesoriaRepository;
    private final UserRepository userRepository;

    public AsesoriaServiceImpl(AsesoriaRepository asesoriaRepository, UserRepository userRepository) {
        this.asesoriaRepository = asesoriaRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AsesoriaResponseDto> listAll() {
        return asesoriaRepository.findAll().stream().map(AsesoriaMapper::toResponse).toList();
    }

    @Override
    public List<AsesoriaResponseDto> listByProgrammer(Long programmerId) {
        return asesoriaRepository.findByProgrammerId(programmerId).stream().map(AsesoriaMapper::toResponse).toList();
    }

    @Override
    public List<AsesoriaResponseDto> listByClient(Long clientId) {
        return asesoriaRepository.findByClientId(clientId).stream().map(AsesoriaMapper::toResponse).toList();
    }

    @Override
    public AsesoriaResponseDto getById(Long id) {
        return asesoriaRepository.findById(id)
            .map(AsesoriaMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Asesoria not found"));
    }

    @Override
    public AsesoriaResponseDto create(CreateAsesoriaDto request) {
        Asesoria asesoria = AsesoriaMapper.toEntity(request);
        asesoria.setProgrammer(userRepository.findById(request.programmerId())
            .orElseThrow(() -> new ResourceNotFoundException("Programmer not found")));
        asesoria.setClient(userRepository.findById(request.clientId())
            .orElseThrow(() -> new ResourceNotFoundException("Client not found")));
        return AsesoriaMapper.toResponse(asesoriaRepository.save(asesoria));
    }

    @Override
    public AsesoriaResponseDto updateStatus(Long id, UpdateAsesoriaStatusDto request) {
        Asesoria asesoria = asesoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asesoria not found"));
        AsesoriaMapper.updateStatus(request, asesoria);
        return AsesoriaMapper.toResponse(asesoriaRepository.save(asesoria));
    }

    @Override
    public void delete(Long id) {
        if (!asesoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Asesoria not found");
        }
        asesoriaRepository.deleteById(id);
    }
}

package com.example.demo.asesorias.controller;

import com.example.demo.asesorias.dto.CreateAsesoriaDto;
import com.example.demo.asesorias.dto.AsesoriaResponseDto;
import com.example.demo.asesorias.dto.UpdateAsesoriaStatusDto;
import com.example.demo.asesorias.service.AsesoriaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asesorias")
public class AsesoriaController {
    private final AsesoriaService asesoriaService;

    public AsesoriaController(AsesoriaService asesoriaService) {
        this.asesoriaService = asesoriaService;
    }

    @GetMapping
    public ResponseEntity<List<AsesoriaResponseDto>> listAll(
        @RequestParam(required = false) Long programmerId,
        @RequestParam(required = false) Long clientId
    ) {
        if (programmerId != null) {
            return ResponseEntity.ok(asesoriaService.listByProgrammer(programmerId));
        }
        if (clientId != null) {
            return ResponseEntity.ok(asesoriaService.listByClient(clientId));
        }
        return ResponseEntity.ok(asesoriaService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsesoriaResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(asesoriaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AsesoriaResponseDto> create(@Valid @RequestBody CreateAsesoriaDto request) {
        return ResponseEntity.ok(asesoriaService.create(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AsesoriaResponseDto> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateAsesoriaStatusDto request) {
        return ResponseEntity.ok(asesoriaService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        asesoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

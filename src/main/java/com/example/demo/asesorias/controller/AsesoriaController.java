package com.example.demo.asesorias.controller;

import com.example.demo.asesorias.dto.AsesoriaResponseDto;
import com.example.demo.asesorias.dto.AsesoriaMyRequestsResponseDto;
import com.example.demo.asesorias.dto.CreateAsesoriaDto;
import com.example.demo.asesorias.dto.UpdateAsesoriaDto;
import com.example.demo.asesorias.dto.UpdateAsesoriaStatusDto;
import com.example.demo.asesorias.dto.CancelAsesoriaDto;
import com.example.demo.asesorias.service.AsesoriaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AsesoriaResponseDto>> listAll(
        @RequestParam(required = false) Long programmerId,
        @RequestParam(required = false) Long clientId
    ) {
        // SOLO ADMIN puede acceder a este endpoint
        // Si se especifican parámetros, utilizarlos; sino devuelve todas
        if (programmerId != null) {
            return ResponseEntity.ok(asesoriaService.listByProgrammer(programmerId));
        }
        if (clientId != null) {
            return ResponseEntity.ok(asesoriaService.listByClient(clientId));
        }
        return ResponseEntity.ok(asesoriaService.listAll());
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AsesoriaMyRequestsResponseDto> getMyRequests() {
        // Devuelve las asesorías del usuario actual divididas en sent y received
        // sent: asesorías donde el usuario actual es cliente
        // received: asesorías donde el usuario actual es programador
        return ResponseEntity.ok(asesoriaService.getMyRequests());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AsesoriaResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(asesoriaService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AsesoriaResponseDto> create(@Valid @RequestBody CreateAsesoriaDto request) {
        // Solo USER puede crear asesorías
        // El usuario logueado será automáticamente el cliente
        return ResponseEntity.ok(asesoriaService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AsesoriaResponseDto> update(@PathVariable("id") Long id, @Valid @RequestBody UpdateAsesoriaDto request) {
        return ResponseEntity.ok(asesoriaService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('PROGRAMADOR')")
    public ResponseEntity<AsesoriaResponseDto> updateStatus(@PathVariable("id") Long id, @Valid @RequestBody UpdateAsesoriaStatusDto request) {
        // Solo PROGRAMADOR puede cambiar el status
        // Validación de ownership se realiza en el service
        return ResponseEntity.ok(asesoriaService.updateStatus(id, request));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AsesoriaResponseDto> cancel(@PathVariable("id") Long id, @Valid @RequestBody CancelAsesoriaDto request) {
        // Solo USER (cliente) puede cancelar una asesoría
        // Validación de ownership se realiza en el service
        return ResponseEntity.ok(asesoriaService.cancel(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'PROGRAMADOR')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        asesoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

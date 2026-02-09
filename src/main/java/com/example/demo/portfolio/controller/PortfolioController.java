package com.example.demo.portfolio.controller;

import com.example.demo.portfolio.dto.CreatePortfolioDto;
import com.example.demo.portfolio.dto.PortfolioResponseDto;
import com.example.demo.portfolio.dto.PublicPortfolioResponseDto;
import com.example.demo.portfolio.dto.UpdatePortfolioDto;
import com.example.demo.portfolio.service.PortfolioService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<List<PortfolioResponseDto>> listAll() {
        return ResponseEntity.ok(portfolioService.listAll());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PROGRAMADOR')")
    public ResponseEntity<PortfolioResponseDto> getMyPortfolio() {
        // El servicio tendrá un método para obtener el portfolio del usuario actual
        return ResponseEntity.ok(portfolioService.getMyPortfolio());
    }

    @GetMapping("/public/{username}")
    public ResponseEntity<PublicPortfolioResponseDto> getPublicByUsername(
        @PathVariable("username") String username
    ) {
        // Endpoint público sin autenticación para ver portafolio + perfil + proyectos
        return ResponseEntity.ok(portfolioService.getPublicByUsername(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(portfolioService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAMADOR')")
    public ResponseEntity<PortfolioResponseDto> create(@Valid @RequestBody CreatePortfolioDto request) {
        return ResponseEntity.ok(portfolioService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAMADOR')")
    public ResponseEntity<PortfolioResponseDto> update(@PathVariable("id") Long id, @Valid @RequestBody UpdatePortfolioDto request) {
        return ResponseEntity.ok(portfolioService.update(id, request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAMADOR')")
    public ResponseEntity<PortfolioResponseDto> patch(@PathVariable("id") Long id, @Valid @RequestBody UpdatePortfolioDto request) {
        return ResponseEntity.ok(portfolioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAMADOR')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        portfolioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

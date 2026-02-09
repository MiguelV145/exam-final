package com.example.demo.portfolio.service;

import com.example.demo.portfolio.dto.CreatePortfolioDto;
import com.example.demo.portfolio.dto.PortfolioResponseDto;
import com.example.demo.portfolio.dto.PublicPortfolioResponseDto;
import com.example.demo.portfolio.dto.UpdatePortfolioDto;
import com.example.demo.portfolio.entity.Portfolio;
import com.example.demo.portfolio.mapper.PortfolioMapper;
import com.example.demo.portfolio.repository.PortfolioRepository;
import com.example.demo.profiles.mapper.ProfileMapper;
import com.example.demo.projects.dto.ProjectResponseDto;
import com.example.demo.projects.service.ProjectService;
import com.example.demo.security.SecurityUtils;
import com.example.demo.shared.exception.BadRequestException;
import com.example.demo.shared.exception.ForbiddenException;
import com.example.demo.shared.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final SecurityUtils securityUtils;
    private final ProjectService projectService;

    public PortfolioServiceImpl(
        PortfolioRepository portfolioRepository,
        SecurityUtils securityUtils,
        ProjectService projectService
    ) {
        this.portfolioRepository = portfolioRepository;
        this.securityUtils = securityUtils;
        this.projectService = projectService;
    }

    @Override
    public List<PortfolioResponseDto> listAll() {
        return portfolioRepository.findAll().stream()
            .map(PortfolioMapper::toResponse)
            .toList();
    }

    @Override
    public PortfolioResponseDto getById(Long id) {
        return portfolioRepository.findById(id)
            .map(PortfolioMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Portafolio no encontrado"));
    }

    @Override
    public PortfolioResponseDto getMyPortfolio() {
        Long currentUserId = securityUtils.getCurrentUserId();
        return portfolioRepository.findByOwnerId(currentUserId)
            .map(PortfolioMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Portafolio no encontrado para el usuario actual"));
    }

    @Override
    public PublicPortfolioResponseDto getPublicByUsername(String username) {
        Portfolio portfolio = portfolioRepository.findByOwnerUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Portafolio no encontrado para el usuario: " + username));
        
        // Obtener el usuario propietario del portafolio
        var owner = portfolio.getOwner();
        var profile = owner.getProfile();
        
        // Validar que el usuario tenga perfil
        if (profile == null) {
            throw new ResourceNotFoundException("Perfil no encontrado para el usuario: " + username);
        }
        
        // Obtener los proyectos del portafolio
        var projects = projectService.listByPortfolio(portfolio.getId());
        
        // Mapear todo a PublicPortfolioResponseDto
        return new PublicPortfolioResponseDto(
            owner.getId(),
            owner.getUsername(),
            ProfileMapper.toResponse(profile),
            PortfolioMapper.toResponse(portfolio),
            projects
        );
    }

    @Override
    @Transactional
    public PortfolioResponseDto create(CreatePortfolioDto request) {
        Long currentUserId = securityUtils.getCurrentUserId();
        
        // Validar que no tenga ya un portfolio (si es PROGRAMADOR)
        if (securityUtils.isProgrammer() && !securityUtils.isAdmin()) {
            if (portfolioRepository.findByOwnerId(currentUserId).isPresent()) {
                throw new BadRequestException("Un programador solo puede tener un portafolio");
            }
        }
        
        Portfolio portfolio = new Portfolio();
        portfolio.setAvailabilityDays(request.availabilityDays());
        portfolio.setAvailabilityStart(request.availabilityStart());
        portfolio.setAvailabilityEnd(request.availabilityEnd());
        
        if (request.skills() != null) {
            portfolio.setSkills(request.skills());
        }
        
        // El owner siempre es el usuario actual (a menos que sea ADMIN)
        portfolio.setOwner(securityUtils.getCurrentUser());
        
        return PortfolioMapper.toResponse(portfolioRepository.save(portfolio));
    }

    @Override
    @Transactional
    public PortfolioResponseDto update(Long id, UpdatePortfolioDto request) {
        Portfolio portfolio = portfolioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Portafolio no encontrado"));
        
        // ADMIN puede editar todo, PROGRAMADOR solo su portfolio
        if (!securityUtils.isAdmin() && 
            !portfolio.getOwner().getId().equals(securityUtils.getCurrentUserId())) {
            throw new ForbiddenException("No tienes permiso para actualizar este portafolio");
        }
        
        if (request.availabilityDays() != null) {
            portfolio.setAvailabilityDays(request.availabilityDays());
        }
        if (request.availabilityStart() != null) {
            portfolio.setAvailabilityStart(request.availabilityStart());
        }
        if (request.availabilityEnd() != null) {
            portfolio.setAvailabilityEnd(request.availabilityEnd());
        }
        if (request.skills() != null) {
            portfolio.setSkills(request.skills());
        }
        
        return PortfolioMapper.toResponse(portfolioRepository.save(portfolio));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Portafolio no encontrado"));
        
        // ADMIN puede eliminar cualquiera, PROGRAMADOR solo su portfolio
        if (!securityUtils.isAdmin() && 
            !portfolio.getOwner().getId().equals(securityUtils.getCurrentUserId())) {
            throw new ForbiddenException("No tienes permiso para eliminar este portafolio");
        }
        
        portfolioRepository.deleteById(id);
    }
}


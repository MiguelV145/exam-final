package com.example.demo.portfolio.dto;

import com.example.demo.profiles.dto.ProfileResponseDto;
import com.example.demo.projects.dto.ProjectResponseDto;
import java.util.List;

/**
 * DTO para la respuesta pública de un portafolio con sus detalles asociados.
 * Contiene información del usuario, perfil, portafolio y proyectos.
 * Este DTO se utiliza para renderizar el Portafolio-Detail en el frontend sin autenticación.
 */
public record PublicPortfolioResponseDto(
    Long userId,
    String username,
    ProfileResponseDto profile,
    PortfolioResponseDto portfolio,
    List<ProjectResponseDto> projects
) {
}

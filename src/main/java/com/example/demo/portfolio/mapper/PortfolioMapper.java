package com.example.demo.portfolio.mapper;

import com.example.demo.portfolio.dto.PortfolioRequest;
import com.example.demo.portfolio.dto.PortfolioResponseDto;
import com.example.demo.portfolio.dto.UpdatePortfolioDto;
import com.example.demo.portfolio.entity.Portfolio;

public final class PortfolioMapper {
    private PortfolioMapper() {
    }

    public static Portfolio toEntity(PortfolioRequest request) {
        Portfolio portfolio = new Portfolio();
        portfolio.setAvailabilityDays(request.availabilityDays());
        portfolio.setAvailabilityStart(request.availabilityStart());
        portfolio.setAvailabilityEnd(request.availabilityEnd());
        portfolio.setSkills(request.skills());
        return portfolio;
    }

    public static void updateEntity(UpdatePortfolioDto request, Portfolio portfolio) {
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
    }

    public static PortfolioResponseDto toResponse(Portfolio portfolio) {
        Long userId = portfolio.getOwner() == null ? null : portfolio.getOwner().getId();
        return new PortfolioResponseDto(
            portfolio.getId(),
            userId,
            portfolio.getAvailabilityDays(),
            portfolio.getAvailabilityStart(),
            portfolio.getAvailabilityEnd(),
            portfolio.getSkills()
        );
    }
}


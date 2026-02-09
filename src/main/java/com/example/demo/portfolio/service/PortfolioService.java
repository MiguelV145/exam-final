package com.example.demo.portfolio.service;

import com.example.demo.portfolio.dto.CreatePortfolioDto;
import com.example.demo.portfolio.dto.PortfolioResponseDto;
import com.example.demo.portfolio.dto.PublicPortfolioResponseDto;
import com.example.demo.portfolio.dto.UpdatePortfolioDto;
import java.util.List;

public interface PortfolioService {
    List<PortfolioResponseDto> listAll();
    PortfolioResponseDto getById(Long id);
    PortfolioResponseDto getMyPortfolio();
    PublicPortfolioResponseDto getPublicByUsername(String username);
    PortfolioResponseDto create(CreatePortfolioDto request);
    PortfolioResponseDto update(Long id, UpdatePortfolioDto request);
    void delete(Long id);
}

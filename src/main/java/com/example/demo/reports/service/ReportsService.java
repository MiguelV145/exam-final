package com.example.demo.reports.service;

import com.example.demo.reports.dto.AsesoriaDayCountDto;
import com.example.demo.reports.dto.AsesoriaProgrammerCountDto;
import com.example.demo.reports.dto.AsesoriaStatusCountDto;
import com.example.demo.reports.dto.ProjectUserCountDto;

import java.time.LocalDate;
import java.util.List;

public interface ReportsService {
    
    List<AsesoriaStatusCountDto> getAsesoriasSummary(LocalDate from, LocalDate to);
    
    List<AsesoriaProgrammerCountDto> getAsesoriasByProgrammer(LocalDate from, LocalDate to);
    
    List<AsesoriaDayCountDto> getAsesoriasByDay(LocalDate from, LocalDate to);
    
    List<ProjectUserCountDto> getProjectsByUser();
}

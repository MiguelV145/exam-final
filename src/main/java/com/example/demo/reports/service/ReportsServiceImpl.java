package com.example.demo.reports.service;

import com.example.demo.asesorias.entity.AsesoriaStatus;
import com.example.demo.asesorias.repository.AsesoriaRepository;
import com.example.demo.projects.repository.ProjectRepository;
import com.example.demo.reports.dto.AsesoriaDayCountDto;
import com.example.demo.reports.dto.AsesoriaProgrammerCountDto;
import com.example.demo.reports.dto.AsesoriaStatusCountDto;
import com.example.demo.reports.dto.ProjectUserCountDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportsServiceImpl implements ReportsService {
    
    private final AsesoriaRepository asesoriaRepository;
    private final ProjectRepository projectRepository;
    
    public ReportsServiceImpl(AsesoriaRepository asesoriaRepository, ProjectRepository projectRepository) {
        this.asesoriaRepository = asesoriaRepository;
        this.projectRepository = projectRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AsesoriaStatusCountDto> getAsesoriasSummary(LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();
        
        List<Object[]> results = asesoriaRepository.countByStatusAndDateRange(fromDateTime, toDateTime);
        
        return results.stream()
            .map(row -> new AsesoriaStatusCountDto((AsesoriaStatus) row[0], (Long) row[1]))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AsesoriaProgrammerCountDto> getAsesoriasByProgrammer(LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();
        
        List<Object[]> results = asesoriaRepository.countByProgrammerAndDateRange(fromDateTime, toDateTime);
        
        return results.stream()
            .map(row -> new AsesoriaProgrammerCountDto(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (Long) row[2]
            ))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AsesoriaDayCountDto> getAsesoriasByDay(LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();
        
        List<Object[]> results = asesoriaRepository.countByDayAndDateRange(fromDateTime, toDateTime);
        
        return results.stream()
            .map(row -> new AsesoriaDayCountDto((LocalDate) row[0], (Long) row[1]))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProjectUserCountDto> getProjectsByUser() {
        // Esta es una implementación simple, podrías mejorarla con una query nativa
        // Agrupa por owner y cuenta total y activos
        List<Object[]> results = projectRepository.countProjectsByUser();
        
        return results.stream()
            .map(row -> new ProjectUserCountDto(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (Long) row[2],
                (Long) row[2] // Por ahora asumimos todos activos, puedes refinar esto
            ))
            .collect(Collectors.toList());
    }
}

package com.example.demo.reports.controller;

import com.example.demo.reports.dto.AsesoriaDayCountDto;
import com.example.demo.reports.dto.AsesoriaProgrammerCountDto;
import com.example.demo.reports.dto.AsesoriaStatusCountDto;
import com.example.demo.reports.dto.ProjectUserCountDto;
import com.example.demo.reports.service.ExportService;
import com.example.demo.reports.service.ReportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Administrative reports and dashboard endpoints")
public class ReportsController {
    
    private final ReportsService reportsService;
    private final ExportService exportService;
    
    public ReportsController(ReportsService reportsService, ExportService exportService) {
        this.reportsService = reportsService;
        this.exportService = exportService;
    }
    
    @GetMapping("/asesorias/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAMADOR')")
    @Operation(summary = "Get asesorias summary by status", 
               description = "Returns count of asesorias grouped by status for a date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public ResponseEntity<List<AsesoriaStatusCountDto>> getAsesoriasSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<AsesoriaStatusCountDto> summary = reportsService.getAsesoriasSummary(from, to);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/asesorias/by-programmer")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAMADOR')")
    @Operation(summary = "Get asesorias by programmer", 
               description = "Returns count of asesorias grouped by programmer for a date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public ResponseEntity<List<AsesoriaProgrammerCountDto>> getAsesoriasByProgrammer(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<AsesoriaProgrammerCountDto> report = reportsService.getAsesoriasByProgrammer(from, to);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/asesorias/by-day")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAMADOR')")
    @Operation(summary = "Get asesorias by day", 
               description = "Returns count of asesorias grouped by day for a date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public ResponseEntity<List<AsesoriaDayCountDto>> getAsesoriasByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<AsesoriaDayCountDto> report = reportsService.getAsesoriasByDay(from, to);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/projects/by-user")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get projects by user", 
               description = "Returns total and active projects grouped by user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public ResponseEntity<List<ProjectUserCountDto>> getProjectsByUser() {
        List<ProjectUserCountDto> report = reportsService.getProjectsByUser();
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/asesorias.xlsx")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Export asesorias to Excel", 
               description = "Exports asesorias data to Excel file for a date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Excel file generated successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public ResponseEntity<byte[]> exportAsesoriasToExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        try {
            byte[] excelData = exportService.exportAsesoriasToExcel(from, to);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                String.format("asesorias_%s_%s.xlsx", 
                    from.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    to.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/asesorias.pdf")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Export asesorias to PDF", 
               description = "Exports asesorias data to PDF file for a date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF file generated successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public ResponseEntity<byte[]> exportAsesoriasToPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        try {
            byte[] pdfData = exportService.exportAsesoriasToPdf(from, to);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                String.format("asesorias_%s_%s.pdf", 
                    from.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    to.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/projects.xlsx")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Export projects to Excel", 
               description = "Exports all projects data to Excel file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Excel file generated successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public ResponseEntity<byte[]> exportProjectsToExcel() {
        try {
            byte[] excelData = exportService.exportProjectsToExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "projects.xlsx");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/projects.pdf")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Export projects to PDF", 
               description = "Exports all projects data to PDF file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF file generated successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public ResponseEntity<byte[]> exportProjectsToPdf() {
        try {
            byte[] pdfData = exportService.exportProjectsToPdf();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "projects.pdf");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

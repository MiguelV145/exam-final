package com.example.demo.reports.service;

import com.example.demo.asesorias.entity.Asesoria;
import com.example.demo.asesorias.repository.AsesoriaRepository;
import com.example.demo.projects.entity.Project;
import com.example.demo.projects.repository.ProjectRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {
    
    private final AsesoriaRepository asesoriaRepository;
    private final ProjectRepository projectRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public ExportService(AsesoriaRepository asesoriaRepository, ProjectRepository projectRepository) {
        this.asesoriaRepository = asesoriaRepository;
        this.projectRepository = projectRepository;
    }
    
    public byte[] exportAsesoriasToExcel(LocalDate from, LocalDate to) throws IOException {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();
        
        List<Asesoria> asesorias = asesoriaRepository.findByStartAtBetween(fromDateTime, toDateTime);
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Asesorías");
            
            // Crear estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Programador", "Cliente", "Fecha/Hora", "Duración (min)", "Modalidad", "Estado", "Comentario"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            int rowNum = 1;
            for (Asesoria asesoria : asesorias) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(asesoria.getId());
                row.createCell(1).setCellValue(asesoria.getProgrammer().getUsername());
                row.createCell(2).setCellValue(asesoria.getClient().getUsername());
                row.createCell(3).setCellValue(asesoria.getStartAt() != null ? 
                    asesoria.getStartAt().format(DATE_FORMATTER) : "N/A");
                row.createCell(4).setCellValue(asesoria.getDurationMinutes() != null ? 
                    asesoria.getDurationMinutes() : 0);
                row.createCell(5).setCellValue(asesoria.getModality() != null ? 
                    asesoria.getModality().toString() : "N/A");
                row.createCell(6).setCellValue(asesoria.getStatus().toString());
                row.createCell(7).setCellValue(asesoria.getComment() != null ? asesoria.getComment() : "");
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    public byte[] exportAsesoriasToPdf(LocalDate from, LocalDate to) throws DocumentException {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();
        
        List<Asesoria> asesorias = asesoriaRepository.findByStartAtBetween(fromDateTime, toDateTime);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate()); // Horizontal para más columnas
        PdfWriter.getInstance(document, out);
        
        document.open();
        
        // Título
        com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Reporte de Asesorías", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Subtítulo con rango de fechas
        com.lowagie.text.Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph subtitle = new Paragraph(
            String.format("Período: %s - %s", from.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                to.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))), 
            subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);
        
        // Tabla
        PdfPTable table = new PdfPTable(7); // 7 columnas
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        
        // Encabezados
        com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        String[] headers = {"ID", "Programador", "Cliente", "Fecha/Hora", "Duración", "Modalidad", "Estado"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(200, 200, 200));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        
        // Datos
        com.lowagie.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        for (Asesoria asesoria : asesorias) {
            table.addCell(new Phrase(String.valueOf(asesoria.getId()), dataFont));
            table.addCell(new Phrase(asesoria.getProgrammer().getUsername(), dataFont));
            table.addCell(new Phrase(asesoria.getClient().getUsername(), dataFont));
            table.addCell(new Phrase(asesoria.getStartAt() != null ? 
                asesoria.getStartAt().format(DATE_FORMATTER) : "N/A", dataFont));
            table.addCell(new Phrase(asesoria.getDurationMinutes() != null ? 
                asesoria.getDurationMinutes() + " min" : "N/A", dataFont));
            table.addCell(new Phrase(asesoria.getModality() != null ? 
                asesoria.getModality().toString() : "N/A", dataFont));
            table.addCell(new Phrase(asesoria.getStatus().toString(), dataFont));
        }
        
        document.add(table);
        
        // Total
        Paragraph total = new Paragraph(
            String.format("\nTotal de asesorías: %d", asesorias.size()), 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        total.setSpacingBefore(20);
        document.add(total);
        
        document.close();
        return out.toByteArray();
    }
    
    public byte[] exportProjectsToExcel() throws IOException {
        List<Project> projects = projectRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Proyectos");
            
            // Crear estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Título", "Descripción", "Owner", "Repo URL", "Demo URL"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            int rowNum = 1;
            for (Project project : projects) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(project.getId());
                row.createCell(1).setCellValue(project.getTitle() != null ? project.getTitle() : "");
                row.createCell(2).setCellValue(project.getDescription() != null ? project.getDescription() : "");
                row.createCell(3).setCellValue(project.getPortfolio() != null && 
                    project.getPortfolio().getOwner() != null ? 
                    project.getPortfolio().getOwner().getUsername() : "N/A");
                row.createCell(4).setCellValue(project.getRepoUrl() != null ? project.getRepoUrl() : "");
                row.createCell(5).setCellValue(project.getDemoUrl() != null ? project.getDemoUrl() : "");
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    public byte[] exportProjectsToPdf() throws DocumentException {
        List<Project> projects = projectRepository.findAll();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        
        document.open();
        
        // Título
        com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Reporte de Proyectos", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Tabla
        PdfPTable table = new PdfPTable(5); // 5 columnas
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        
        // Encabezados
        com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        String[] headers = {"ID", "Título", "Descripción", "Owner", "Repo URL"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(200, 200, 200));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        
        // Datos
        com.lowagie.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        for (Project project : projects) {
            table.addCell(new Phrase(String.valueOf(project.getId()), dataFont));
            table.addCell(new Phrase(project.getTitle() != null ? project.getTitle() : "", dataFont));
            table.addCell(new Phrase(project.getDescription() != null ? 
                (project.getDescription().length() > 50 ? 
                    project.getDescription().substring(0, 50) + "..." : project.getDescription()) : "", dataFont));
            table.addCell(new Phrase(project.getPortfolio() != null && 
                project.getPortfolio().getOwner() != null ? 
                project.getPortfolio().getOwner().getUsername() : "N/A", dataFont));
            table.addCell(new Phrase(project.getRepoUrl() != null ? project.getRepoUrl() : "", dataFont));
        }
        
        document.add(table);
        
        // Total
        Paragraph total = new Paragraph(
            String.format("\nTotal de proyectos: %d", projects.size()), 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        total.setSpacingBefore(20);
        document.add(total);
        
        document.close();
        return out.toByteArray();
    }
}

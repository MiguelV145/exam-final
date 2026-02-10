package com.example.demo.asesorias.scheduler;

import com.example.demo.asesorias.entity.Asesoria;
import com.example.demo.asesorias.repository.AsesoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AsesoriaReminderScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(AsesoriaReminderScheduler.class);
    
    private final AsesoriaRepository asesoriaRepository;
    
    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;
    
    public AsesoriaReminderScheduler(AsesoriaRepository asesoriaRepository) {
        this.asesoriaRepository = asesoriaRepository;
    }
    
    /**
     * DESACTIVADO: Backend ya no envía emails. Frontend usa EmailJS.
     * Este scheduler está deshabilitado por app.email.enabled=false
     */
    @Scheduled(fixedRate = 60000) // Cada 60 segundos (1 minuto)
    @Transactional
    public void sendAsesoriaReminders() {
        if (!emailEnabled) {
            logger.debug("Email sending disabled. Reminder scheduler is inactive.");
            return;
        }
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.plusMinutes(15);
            LocalDateTime end = now.plusMinutes(16);
            
            List<Asesoria> asesorias = asesoriaRepository.findAsesoriasForReminder(start, end);
            
            if (asesorias.isEmpty()) {
                logger.trace("No asesorias found for reminder at {}", now);
                return;
            }
            
            logger.info("Found {} asesorias for reminder but email is disabled", asesorias.size());
            
        } catch (Exception e) {
            logger.error("Error in reminder scheduler: {}", e.getMessage(), e);
        }
    }
}

package com.example.demo.availability.dto;

import com.example.demo.availability.entity.Modality;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class AvailabilitySlotCreateDto {
    
    @NotNull(message = "El día de la semana es requerido")
    private DayOfWeekEs dayOfWeek;
    
    @NotNull(message = "La hora de inicio es requerida")
    private LocalTime startTime;
    
    @NotNull(message = "La hora de fin es requerida")
    private LocalTime endTime;
    
    @NotNull(message = "La modalidad es requerida")
    private Modality modality;
    
    private Boolean enabled = true;

    public AvailabilitySlotCreateDto() {
    }

    public DayOfWeekEs getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeekEs dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Modality getModality() {
        return modality;
    }

    public void setModality(Modality modality) {
        this.modality = modality;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}

package com.example.demo.availability.dto;

import com.example.demo.availability.entity.Modality;
import java.time.LocalTime;

public class AvailabilitySlotUpdateDto {
    
    private DayOfWeekEs dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Modality modality;
    private Boolean enabled;

    public AvailabilitySlotUpdateDto() {
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

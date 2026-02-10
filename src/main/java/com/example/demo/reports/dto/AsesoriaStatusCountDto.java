package com.example.demo.reports.dto;

import com.example.demo.asesorias.entity.AsesoriaStatus;

public class AsesoriaStatusCountDto {
    private AsesoriaStatus status;
    private Long count;

    public AsesoriaStatusCountDto() {
    }

    public AsesoriaStatusCountDto(AsesoriaStatus status, Long count) {
        this.status = status;
        this.count = count;
    }

    public AsesoriaStatus getStatus() {
        return status;
    }

    public void setStatus(AsesoriaStatus status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

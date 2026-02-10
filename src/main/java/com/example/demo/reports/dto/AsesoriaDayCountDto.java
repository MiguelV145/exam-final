package com.example.demo.reports.dto;

import java.time.LocalDate;

public class AsesoriaDayCountDto {
    private LocalDate date;
    private Long count;

    public AsesoriaDayCountDto() {
    }

    public AsesoriaDayCountDto(LocalDate date, Long count) {
        this.date = date;
        this.count = count;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

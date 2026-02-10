package com.example.demo.reports.dto;

public class AsesoriaProgrammerCountDto {
    private Long programmerId;
    private String programmerUsername;
    private Long count;

    public AsesoriaProgrammerCountDto() {
    }

    public AsesoriaProgrammerCountDto(Long programmerId, String programmerUsername, Long count) {
        this.programmerId = programmerId;
        this.programmerUsername = programmerUsername;
        this.count = count;
    }

    public Long getProgrammerId() {
        return programmerId;
    }

    public void setProgrammerId(Long programmerId) {
        this.programmerId = programmerId;
    }

    public String getProgrammerUsername() {
        return programmerUsername;
    }

    public void setProgrammerUsername(String programmerUsername) {
        this.programmerUsername = programmerUsername;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

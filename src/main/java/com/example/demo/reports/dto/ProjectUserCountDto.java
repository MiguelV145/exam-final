package com.example.demo.reports.dto;

public class ProjectUserCountDto {
    private Long userId;
    private String username;
    private Long totalProjects;
    private Long activeProjects;

    public ProjectUserCountDto() {
    }

    public ProjectUserCountDto(Long userId, String username, Long totalProjects, Long activeProjects) {
        this.userId = userId;
        this.username = username;
        this.totalProjects = totalProjects;
        this.activeProjects = activeProjects;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(Long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public Long getActiveProjects() {
        return activeProjects;
    }

    public void setActiveProjects(Long activeProjects) {
        this.activeProjects = activeProjects;
    }
}

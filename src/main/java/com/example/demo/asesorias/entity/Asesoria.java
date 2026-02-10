package com.example.demo.asesorias.entity;

import com.example.demo.availability.entity.Modality;
import com.example.demo.users.entity.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "asesorias")
public class Asesoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos legacy (compatibilidad hacia atrás)
    @Column(nullable = true)
    private LocalDate date;

    @Column(nullable = true)
    private LocalTime time;

    // Nuevos campos
    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes = 60;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Modality modality;

    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent = false;

    @Column(length = 1000)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AsesoriaStatus status;

    @Column(name = "response_msg", length = 1000)
    private String responseMsg;

    @ManyToOne
    @JoinColumn(name = "programmer_id", nullable = false)
    private User programmer;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Si usan date/time legacy, sincronizar con startAt
        if (startAt == null && date != null && time != null) {
            startAt = LocalDateTime.of(date, time);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Asesoria() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public AsesoriaStatus getStatus() {
        return status;
    }

    public void setStatus(AsesoriaStatus status) {
        this.status = status;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public User getProgrammer() {
        return programmer;
    }

    public void setProgrammer(User programmer) {
        this.programmer = programmer;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Modality getModality() {
        return modality;
    }

    public void setModality(Modality modality) {
        this.modality = modality;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

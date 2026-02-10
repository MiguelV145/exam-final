package com.example.demo.asesorias.repository;

import com.example.demo.asesorias.entity.Asesoria;
import com.example.demo.asesorias.entity.AsesoriaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsesoriaRepository extends JpaRepository<Asesoria, Long> {

    // Buscar asesoría por ID (heredado de JpaRepository)
    Optional<Asesoria> findById(Long id);
    
    // Buscar asesorías por programador
    List<Asesoria> findByProgrammerId(Long programmerId);
    
    // Buscar asesorías por cliente
    List<Asesoria> findByClientId(Long clientId);
    
    // Validar si una asesoría pertenece a un programador específico
    boolean existsByIdAndProgrammerId(Long id, Long programmerId);
    
    // Validar si una asesoría pertenece a un cliente específico
    boolean existsByIdAndClientId(Long id, Long clientId);
    
    // Buscar asesorías que se cruzan con un horario dado
    @Query("SELECT a FROM Asesoria a WHERE a.programmer.id = :programmerId " +
           "AND a.startAt IS NOT NULL " +
           "AND a.status IN ('CREATED', 'CONFIRMED') " +
           "AND ((a.startAt <= :startTime AND FUNCTION('DATEADD', MINUTE, a.durationMinutes, a.startAt) > :startTime) " +
           "OR (a.startAt < :endTime AND FUNCTION('DATEADD', MINUTE, a.durationMinutes, a.startAt) >= :endTime) " +
           "OR (a.startAt >= :startTime AND a.startAt < :endTime))")
    List<Asesoria> findConflictingAsesorias(
        @Param("programmerId") Long programmerId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    // Buscar asesorías CONFIRMED listas para recordatorio (15-16 min antes)
    @Query("SELECT a FROM Asesoria a WHERE a.status = 'CONFIRMED' " +
           "AND a.reminderSent = false " +
           "AND a.startAt BETWEEN :start AND :end")
    List<Asesoria> findAsesoriasForReminder(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    // Buscar asesorías por rango de fechas y status (para reportes)
    @Query("SELECT a FROM Asesoria a WHERE a.startAt BETWEEN :from AND :to")
    List<Asesoria> findByStartAtBetween(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );
    
    // Conteo por status y rango de fechas
    @Query("SELECT a.status, COUNT(a) FROM Asesoria a " +
           "WHERE a.startAt BETWEEN :from AND :to " +
           "GROUP BY a.status")
    List<Object[]> countByStatusAndDateRange(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );
    
    // Conteo por programador y rango de fechas
    @Query("SELECT a.programmer.id, a.programmer.username, COUNT(a) FROM Asesoria a " +
           "WHERE a.startAt BETWEEN :from AND :to " +
           "GROUP BY a.programmer.id, a.programmer.username " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> countByProgrammerAndDateRange(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );
    
    // Conteo por día
    @Query("SELECT CAST(a.startAt AS date), COUNT(a) FROM Asesoria a " +
           "WHERE a.startAt BETWEEN :from AND :to " +
           "GROUP BY CAST(a.startAt AS date) " +
           "ORDER BY CAST(a.startAt AS date)")
    List<Object[]> countByDayAndDateRange(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );
}


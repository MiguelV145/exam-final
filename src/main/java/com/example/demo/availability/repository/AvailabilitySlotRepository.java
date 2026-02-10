package com.example.demo.availability.repository;

import com.example.demo.availability.entity.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    
    List<AvailabilitySlot> findByProgrammerId(Long programmerId);
    
    List<AvailabilitySlot> findByProgrammerIdAndEnabledTrue(Long programmerId);
    
    @Query("SELECT a FROM AvailabilitySlot a WHERE a.programmer.id = :programmerId " +
           "AND a.dayOfWeek = :dayOfWeek AND a.enabled = true " +
           "AND ((a.startTime <= :startTime AND a.endTime > :startTime) " +
           "OR (a.startTime < :endTime AND a.endTime >= :endTime) " +
           "OR (a.startTime >= :startTime AND a.endTime <= :endTime))")
    List<AvailabilitySlot> findOverlappingSlots(
        @Param("programmerId") Long programmerId,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );
    
    @Query("SELECT a FROM AvailabilitySlot a WHERE a.programmer.id = :programmerId " +
           "AND a.dayOfWeek = :dayOfWeek AND a.enabled = true " +
           "AND a.startTime <= :time AND a.endTime > :time")
    List<AvailabilitySlot> findSlotCoveringTime(
        @Param("programmerId") Long programmerId,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("time") LocalTime time
    );
}

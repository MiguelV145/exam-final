package com.example.demo.availability.dto;

import java.time.DayOfWeek;

public enum DayOfWeekEs {
    LUNES,
    MARTES,
    MIERCOLES,
    JUEVES,
    VIERNES,
    SABADO,
    DOMINGO;

    public DayOfWeek toJava() {
        return switch (this) {
            case LUNES -> DayOfWeek.MONDAY;
            case MARTES -> DayOfWeek.TUESDAY;
            case MIERCOLES -> DayOfWeek.WEDNESDAY;
            case JUEVES -> DayOfWeek.THURSDAY;
            case VIERNES -> DayOfWeek.FRIDAY;
            case SABADO -> DayOfWeek.SATURDAY;
            case DOMINGO -> DayOfWeek.SUNDAY;
        };
    }

    public static DayOfWeekEs fromJava(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            return null;
        }

        return switch (dayOfWeek) {
            case MONDAY -> LUNES;
            case TUESDAY -> MARTES;
            case WEDNESDAY -> MIERCOLES;
            case THURSDAY -> JUEVES;
            case FRIDAY -> VIERNES;
            case SATURDAY -> SABADO;
            case SUNDAY -> DOMINGO;
        };
    }
}

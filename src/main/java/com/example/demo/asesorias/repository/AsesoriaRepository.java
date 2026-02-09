package com.example.demo.asesorias.repository;

import com.example.demo.asesorias.entity.Asesoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}

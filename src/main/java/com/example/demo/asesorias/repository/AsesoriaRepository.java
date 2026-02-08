package com.example.demo.asesorias.repository;

import com.example.demo.asesorias.entity.Asesoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsesoriaRepository extends JpaRepository<Asesoria, Long> {
    List<Asesoria> findByProgrammerId(Long programmerId);
    List<Asesoria> findByClientId(Long clientId);
}

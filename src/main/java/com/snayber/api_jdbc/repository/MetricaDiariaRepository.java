package com.snayber.api_jdbc.repository;

import com.snayber.api_jdbc.model.MetricaDiaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MetricaDiariaRepository extends JpaRepository<MetricaDiaria, Long> {
    
    Optional<MetricaDiaria> findByFecha(LocalDate fecha);
    
    List<MetricaDiaria> findByFechaBetweenOrderByFechaDesc(LocalDate fechaInicio, LocalDate fechaFin);
    
    @Query("SELECT m FROM MetricaDiaria m WHERE m.fecha >= :fecha ORDER BY m.fecha DESC")
    List<MetricaDiaria> findUltimosDias(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT m FROM MetricaDiaria m WHERE FUNCTION('TO_CHAR', m.fecha, 'YYYY-MM') = :mes ORDER BY m.fecha")
    List<MetricaDiaria> findByMes(@Param("mes") String mes);
}

package com.snayber.api_jdbc.repository;

import com.snayber.api_jdbc.model.HistorialReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialReporteRepository extends JpaRepository<HistorialReporte, Long> {
    
    List<HistorialReporte> findByUsuarioIdOrderByFechaGeneracionDesc(Long usuarioId);
    
    List<HistorialReporte> findByTipoReporteOrderByFechaGeneracionDesc(String tipoReporte);
    
    @Query("SELECT h FROM HistorialReporte h WHERE h.fechaGeneracion BETWEEN :inicio AND :fin ORDER BY h.fechaGeneracion DESC")
    List<HistorialReporte> findByRangoFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT h FROM HistorialReporte h ORDER BY h.fechaGeneracion DESC")
    List<HistorialReporte> findTop10ByOrderByFechaGeneracionDesc();
}

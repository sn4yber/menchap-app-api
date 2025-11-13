package com.snayber.api_jdbc.repository;

import com.snayber.api_jdbc.model.AlertaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaInventarioRepository extends JpaRepository<AlertaInventario, Long> {
    
    List<AlertaInventario> findByResuelta(Boolean resuelta);
    
    List<AlertaInventario> findByProductoIdOrderByFechaAlertaDesc(Long productoId);
    
    List<AlertaInventario> findByProductoIdAndResuelta(Long productoId, Boolean resuelta);
    
    @Query("SELECT a FROM AlertaInventario a WHERE a.resuelta = false ORDER BY " +
           "CASE a.nivelSeveridad " +
           "WHEN 'CRITICO' THEN 1 " +
           "WHEN 'ALTO' THEN 2 " +
           "WHEN 'MEDIO' THEN 3 " +
           "ELSE 4 END, a.fechaAlerta DESC")
    List<AlertaInventario> findAlertasActivasOrdenadas();
    
    Long countByResuelta(Boolean resuelta);
    
    Long countByResueltaFalse();
    
    Long countByNivelSeveridadAndResuelta(String nivelSeveridad, Boolean resuelta);
}

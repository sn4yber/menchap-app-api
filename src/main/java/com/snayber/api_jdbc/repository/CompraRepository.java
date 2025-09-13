package com.snayber.api_jdbc.repository;

import com.snayber.api_jdbc.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByProductoId(Long productoId);
    List<Compra> findByFechaCompraBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Compra> findByProveedor(String proveedor);
    List<Compra> findByMetodoPago(String metodoPago);
    List<Compra> findByNumeroFactura(String numeroFactura);

    @Query("SELECT COALESCE(SUM(c.costoTotal), 0) FROM Compra c WHERE c.fechaCompra BETWEEN :inicio AND :fin")
    BigDecimal calcularTotalComprasPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT c FROM Compra c WHERE CAST(c.fechaCompra AS date) = CURRENT_DATE ORDER BY c.fechaCompra DESC")
    List<Compra> findComprasHoy();
}

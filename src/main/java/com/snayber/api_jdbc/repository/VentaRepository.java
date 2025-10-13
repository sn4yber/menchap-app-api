package com.snayber.api_jdbc.repository;

import com.snayber.api_jdbc.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByProductoId(Long productoId);
    List<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Venta> findByCliente(String cliente);
    List<Venta> findByMetodoPago(String metodoPago);

    @Query("SELECT COALESCE(SUM(v.precioTotal), 0) FROM Venta v")
    BigDecimal calcularTotalVentas();

    @Query("SELECT COALESCE(SUM(v.ganancia), 0) FROM Venta v")
    BigDecimal calcularTotalGanancias();

    @Query("SELECT COALESCE(SUM(v.precioTotal), 0) FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal calcularTotalVentasPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(v.ganancia), 0) FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal calcularGananciasPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT v FROM Venta v WHERE CAST(v.fechaVenta AS date) = CURRENT_DATE ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasHoy();
}

package com.snayber.api_jdbc.repository;

import com.snayber.api_jdbc.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByNombre(String nombre);
    Optional<Producto> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
    List<Producto> findByTipo(String tipo);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCantidadGreaterThan(BigDecimal cantidad);
    List<Producto> findByPrecioGreaterThan(BigDecimal precio);
    List<Producto> findByPrecioLessThan(BigDecimal precio);
    List<Producto> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    @Query("SELECT COALESCE(SUM(p.cantidad * p.precio), 0) FROM Producto p")
    BigDecimal calcularValorTotalInventario();

    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.cantidad = p.cantidad + :add, p.version = p.version + 1 WHERE p.id = :id AND p.version = :version")
    int incrementCantidadSeguro(@Param("id") Long id, @Param("add") BigDecimal add, @Param("version") Long version);
    
    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.cantidad = p.cantidad + :add WHERE p.id = :id")
    int incrementCantidad(@Param("id") Long id, @Param("add") BigDecimal add);
    
    @Query("SELECT p.version FROM Producto p WHERE p.id = :id")
    Optional<Long> findVersionById(@Param("id") Long id);
}

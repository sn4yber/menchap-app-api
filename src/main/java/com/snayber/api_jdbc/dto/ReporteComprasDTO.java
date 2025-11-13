package com.snayber.api_jdbc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteComprasDTO {
    private Long productoId;
    private String nombreProducto;
    private String proveedor;
    private BigDecimal cantidadComprada;
    private BigDecimal totalCompras;
    private Long numeroTransacciones;
    private BigDecimal costoPromedio;
}

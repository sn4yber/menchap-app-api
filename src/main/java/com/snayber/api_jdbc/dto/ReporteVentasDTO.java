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
public class ReporteVentasDTO {
    private Long productoId;
    private String nombreProducto;
    private BigDecimal cantidadVendida;
    private BigDecimal totalVentas;
    private BigDecimal gananciaTotal;
    private Long numeroTransacciones;
    private BigDecimal margenPromedio;
}

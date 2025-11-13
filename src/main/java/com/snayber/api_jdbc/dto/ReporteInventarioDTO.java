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
public class ReporteInventarioDTO {
    private Long productoId;
    private String nombre;
    private String tipo;
    private BigDecimal cantidad;
    private BigDecimal precioVenta;
    private BigDecimal costoPromedio;
    private BigDecimal valorVenta;
    private BigDecimal valorCosto;
    private BigDecimal gananciaPotencial;
    private String estadoStock;
}

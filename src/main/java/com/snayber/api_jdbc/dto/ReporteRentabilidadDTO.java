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
public class ReporteRentabilidadDTO {
    private Long productoId;
    private String nombreProducto;
    private BigDecimal totalVendido;
    private BigDecimal totalComprado;
    private BigDecimal gananciaNeta;
    private BigDecimal margenPorcentaje;
    private BigDecimal roiPorcentaje;
}

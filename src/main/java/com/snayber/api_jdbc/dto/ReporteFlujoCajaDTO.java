package com.snayber.api_jdbc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteFlujoCajaDTO {
    private LocalDate fecha;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private BigDecimal saldoDia;
    private BigDecimal saldoAcumulado;
}

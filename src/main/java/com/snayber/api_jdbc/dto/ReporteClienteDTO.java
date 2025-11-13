package com.snayber.api_jdbc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteClienteDTO {
    private String cliente;
    private Long numeroCompras;
    private BigDecimal totalGastado;
    private BigDecimal ticketPromedio;
    private LocalDateTime ultimaCompra;
}

package com.snayber.api_jdbc.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumenFinanciero {
    
    private LocalDate fecha;
    private LocalDateTime fechaActualizacion;
    private BigDecimal totalVentas;
    private BigDecimal totalCompras;
    private BigDecimal gananciaBruta;
    private BigDecimal gananciasNetas;
    private BigDecimal valorInventario;
    private BigDecimal margenPromedio;
    private Integer cantidadVentas;
    private Integer cantidadCompras;
    private BigDecimal inventarioTotal;
    private String productoMasVendido;
    private BigDecimal ventaPromedio;

    public BigDecimal calcularGananciaNeta() {
        if (totalVentas != null && totalCompras != null) {
            return totalVentas.subtract(totalCompras);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calcularROI() {
        if (totalCompras != null && totalCompras.compareTo(BigDecimal.ZERO) > 0 && gananciaBruta != null) {
            return gananciaBruta.divide(totalCompras, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        }
        return BigDecimal.ZERO;
    }
}

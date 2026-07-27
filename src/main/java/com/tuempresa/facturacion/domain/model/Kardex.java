package com.tuempresa.facturacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Kardex {
    private Long id;
    private Long productoId;
    private String tipoMovimiento; // COMPRA, VENTA, AJUSTE
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal stockResultante;
    private LocalDateTime creadoEn;
}

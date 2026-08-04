package com.tuempresa.facturacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CobroPago {
    private Long id;
    private Long comprobanteId;
    private BigDecimal monto;
    private String metodoPago;
    private LocalDate fechaPago;
    private String referencia;
    private LocalDateTime creadoEn;
}

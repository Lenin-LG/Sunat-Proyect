package com.tuempresa.facturacion.domain.ports.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CobroPagoCommand {
    private Long comprobanteId;
    private BigDecimal monto;
    private String metodoPago;
    private LocalDate fechaPago;
    private String referencia;
}

package com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CobroPagoRequest {
    private Long comprobanteId;
    private BigDecimal monto;
    private String metodoPago;
    private LocalDate fechaPago;
    private String referencia;
}

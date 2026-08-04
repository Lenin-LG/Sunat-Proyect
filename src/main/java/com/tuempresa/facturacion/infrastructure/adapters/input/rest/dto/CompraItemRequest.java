package com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CompraItemRequest {
    private Long productoId;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
}

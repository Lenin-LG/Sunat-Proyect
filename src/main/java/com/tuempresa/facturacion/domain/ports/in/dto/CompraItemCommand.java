package com.tuempresa.facturacion.domain.ports.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraItemCommand {
    private Long productoId;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
}

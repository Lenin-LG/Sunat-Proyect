package com.tuempresa.facturacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraDetalle {
    private Long id;
    private Long compraId;
    private Long productoId;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
}

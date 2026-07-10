package com.tuempresa.facturacion.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ComprobanteDetalle {
    private Long id;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private String codigoProductoSunat;

    public BigDecimal getValorVenta() {
        if (cantidad == null || precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        return cantidad.multiply(precioUnitario);
    }
}

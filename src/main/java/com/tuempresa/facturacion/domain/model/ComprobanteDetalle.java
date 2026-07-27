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
    private String codigoInterno;
    private String tipoUnidad = "NIU";
    private String tipoAfectacionIgv = "10"; // Gravado - Operación Onerosa
    private BigDecimal impuestoBolsa = BigDecimal.ZERO;

    public BigDecimal getValorVenta() {
        if (cantidad == null || precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        return cantidad.multiply(precioUnitario);
    }
}

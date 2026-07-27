package com.tuempresa.facturacion.domain.ports.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCommand {
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private String codigoProductoSunat;
    private String codigoInterno;
    @Builder.Default
    private String tipoUnidad = "NIU";
    @Builder.Default
    private String tipoAfectacionIgv = "10";
    @Builder.Default
    private BigDecimal impuestoBolsa = BigDecimal.ZERO;
}

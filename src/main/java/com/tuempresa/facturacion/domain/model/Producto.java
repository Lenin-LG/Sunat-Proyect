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
public class Producto {
    private Long id;
    private String codigo;
    private String descripcion;
    private BigDecimal precioUnitario;
    private String tipoAfectacionIgvId;
    private String unidadMedidaId;
    private BigDecimal stockActual;
}

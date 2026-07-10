package com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComprobanteItemRequest {

    @NotBlank
    private String descripcion;

    @NotNull
    @Positive
    private BigDecimal cantidad;

    @NotNull
    @Positive
    private BigDecimal precioUnitario;

    private String codigoProductoSunat;
}

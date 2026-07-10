package com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ComprobanteRequest {

    @NotBlank
    private String serie;

    private Integer numero;

    @NotBlank
    private String clienteTipoDocumento;

    @NotBlank
    private String clienteNumeroDocumento;

    @NotBlank
    private String clienteNombre;

    @NotNull
    @NotEmpty
    private List<@Valid ComprobanteItemRequest> items;
}

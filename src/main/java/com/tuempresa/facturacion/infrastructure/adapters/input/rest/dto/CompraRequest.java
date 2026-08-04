package com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CompraRequest {
    private String tipoDocumento;
    private String serie;
    private Integer numero;
    private Long proveedorId;
    private LocalDate fechaEmision;
    private List<CompraItemRequest> items;
}

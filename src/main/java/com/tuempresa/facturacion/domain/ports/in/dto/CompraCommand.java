package com.tuempresa.facturacion.domain.ports.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraCommand {
    private String tipoDocumento;
    private String serie;
    private Integer numero;
    private Long proveedorId;
    private LocalDate fechaEmision;
    private List<CompraItemCommand> items;
}

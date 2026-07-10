package com.tuempresa.facturacion.domain.ports.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteCommand {
    private String tipoDocumento;
    private String serie;
    private String clienteTipoDocumento;
    private String clienteNumeroDocumento;
    private String clienteNombre;
    private List<ItemCommand> items;
}

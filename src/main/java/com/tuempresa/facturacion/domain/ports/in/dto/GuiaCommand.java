package com.tuempresa.facturacion.domain.ports.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuiaCommand {
    private String tipoGuia; // "09" = Remitente, "31" = Transportista
    private String serie;
    private Long comprobanteId;
    private Long clienteId;
    private Long choferId;
    private Long vehiculoId;
    private String motivoTraslado;
    private BigDecimal pesoTotal;
}

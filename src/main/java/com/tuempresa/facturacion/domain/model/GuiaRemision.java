package com.tuempresa.facturacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuiaRemision {
    private Long id;
    private String tipoGuia; // "09" = Remitente, "31" = Transportista
    private String serie;
    private Integer numero;
    private LocalDate fechaEmision;
    private Long comprobanteId;
    private Long clienteId;
    private Long choferId;
    private Long vehiculoId;
    private String motivoTraslado;
    private BigDecimal pesoTotal;
    private String sunatResponseCode;
    private String sunatDescription;
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
}

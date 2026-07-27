package com.tuempresa.facturacion.domain.ports.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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

    // Advanced Billing fields
    @Builder.Default
    private String formaPago = "CONTADO";
    private String detraccionCodigo;
    private BigDecimal detraccionPorcentaje;
    private BigDecimal detraccionMonto;
    @Builder.Default
    private BigDecimal descuentoGlobal = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal totalImpuestoBolsa = BigDecimal.ZERO;
    private String anticipoReferencia;
    @Builder.Default
    private BigDecimal saldoPendiente = BigDecimal.ZERO;
    private List<CuotaCommand> cuotas;

    // Notes Support
    private String documentoModificadoId;
    private String documentoModificadoTipo;
    private String notaMotivoCodigo;
    private String notaMotivoDescripcion;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuotaCommand {
        private Integer numeroCuota;
        private BigDecimal monto;
        private java.time.LocalDate fechaVencimiento;
    }
}

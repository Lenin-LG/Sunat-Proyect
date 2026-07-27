package com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
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

    // Advanced Billing fields
    private String formaPago = "CONTADO"; // CONTADO, CREDITO
    private String detraccionCodigo;
    private BigDecimal detraccionPorcentaje;
    private BigDecimal detraccionMonto;
    private BigDecimal descuentoGlobal = BigDecimal.ZERO;
    private BigDecimal totalImpuestoBolsa = BigDecimal.ZERO;
    private String anticipoReferencia;
    private BigDecimal saldoPendiente = BigDecimal.ZERO;
    private List<CuotaRequest> cuotas;

    // Notes Support (Nota de Crédito/Débito)
    private String documentoModificadoId;
    private String documentoModificadoTipo;
    private String notaMotivoCodigo;
    private String notaMotivoDescripcion;

    @Data
    public static class CuotaRequest {
        private Integer numeroCuota;
        private BigDecimal monto;
        private String fechaVencimiento; // YYYY-MM-DD
    }
}

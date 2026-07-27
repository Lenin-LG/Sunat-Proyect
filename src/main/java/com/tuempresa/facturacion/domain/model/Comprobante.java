package com.tuempresa.facturacion.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Comprobante {

    private Long id;
    private String tipoDocumento;
    private String serie;
    private Integer numero;
    private LocalDate fechaEmision;
    private String clienteTipoDocumento;
    private String clienteNumeroDocumento;
    private String clienteNombre;
    private BigDecimal totalGravada = BigDecimal.ZERO;
    private BigDecimal totalIgv = BigDecimal.ZERO;
    private BigDecimal totalPagar = BigDecimal.ZERO;
    private EstadoComprobante estado = EstadoComprobante.PENDIENTE;
    private String sunatResponseCode;
    private String sunatDescription;
    private LocalDateTime creadoEn = LocalDateTime.now();
    private LocalDateTime enviadoEn;
    private List<ComprobanteDetalle> detalles = new ArrayList<>();

    // Campos Avanzados CPE
    private String formaPago = "CONTADO"; // CONTADO, CREDITO
    private String detraccionCodigo;
    private BigDecimal detraccionPorcentaje;
    private BigDecimal detraccionMonto;
    private BigDecimal descuentoGlobal = BigDecimal.ZERO;
    private BigDecimal totalImpuestoBolsa = BigDecimal.ZERO;
    private String anticipoReferencia;
    private BigDecimal saldoPendiente = BigDecimal.ZERO;
    private List<Cuota> cuotas = new ArrayList<>();

    // Notas de Crédito / Débito
    private String documentoModificadoId;     // ej. F001-45
    private String documentoModificadoTipo;   // ej. 01 (Factura)
    private String notaMotivoCodigo;          // ej. 01 (Anulación de la operación)
    private String notaMotivoDescripcion;     // ej. ANULACION POR ERROR DE DATOS

    public String getNombreArchivo(String ruc) {
        return "%s-%s-%s-%d".formatted(ruc, tipoDocumento, serie, numero);
    }

    public enum EstadoComprobante {
        PENDIENTE, ENVIADO, ACEPTADO, RECHAZADO, ERROR
    }
}

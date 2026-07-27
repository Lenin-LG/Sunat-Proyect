package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity;

import com.tuempresa.facturacion.domain.model.Comprobante.EstadoComprobante;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comprobantes")
@Getter
@Setter
@NoArgsConstructor
public class ComprobanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2)
    private String tipoDocumento;

    @Column(nullable = false, length = 4)
    private String serie;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false)
    private LocalDate fechaEmision;

    @Column(nullable = false, length = 1)
    private String clienteTipoDocumento;

    @Column(nullable = false, length = 15)
    private String clienteNumeroDocumento;

    @Column(nullable = false)
    private String clienteNombre;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalGravada = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalIgv = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPagar = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoComprobante estado = EstadoComprobante.PENDIENTE;

    private String sunatResponseCode;

    @Column(length = 1000)
    private String sunatDescription;

    @Column(updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime enviadoEn;

    @OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprobanteDetalleEntity> detalles = new ArrayList<>();

    @Column(name = "forma_pago", nullable = false)
    private String formaPago = "CONTADO";

    @Column(name = "detraccion_codigo")
    private String detraccionCodigo;

    @Column(name = "detraccion_porcentaje", precision = 5, scale = 2)
    private BigDecimal detraccionPorcentaje;

    @Column(name = "detraccion_monto", precision = 12, scale = 2)
    private BigDecimal detraccionMonto;

    @Column(name = "descuento_global", nullable = false, precision = 12, scale = 2)
    private BigDecimal descuentoGlobal = BigDecimal.ZERO;

    @Column(name = "total_impuesto_bolsa", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalImpuestoBolsa = BigDecimal.ZERO;

    @Column(name = "anticipo_referencia")
    private String anticipoReferencia;

    @Column(name = "saldo_pendiente", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoPendiente = BigDecimal.ZERO;

    @OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CuotaEntity> cuotas = new ArrayList<>();

    @Column(name = "documento_modificado_id")
    private String documentoModificadoId;

    @Column(name = "documento_modificado_tipo", length = 2)
    private String documentoModificadoTipo;

    @Column(name = "nota_motivo_codigo")
    private String notaMotivoCodigo;

    @Column(name = "nota_motivo_descripcion")
    private String notaMotivoDescripcion;
}

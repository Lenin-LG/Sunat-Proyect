package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "comprobante_detalles")
@Getter
@Setter
@NoArgsConstructor
public class ComprobanteDetalleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id", nullable = false)
    private ComprobanteEntity comprobante;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal cantidad;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal precioUnitario;

    private String codigoProductoSunat;

    @Column(name = "tipo_unidad", nullable = false)
    private String tipoUnidad = "NIU";

    @Column(name = "tipo_afectacion_igv", nullable = false)
    private String tipoAfectacionIgv = "10";

    @Column(name = "impuesto_bolsa", nullable = false, precision = 12, scale = 2)
    private BigDecimal impuestoBolsa = BigDecimal.ZERO;

    @Column(name = "codigo_interno")
    private String codigoInterno;
}

package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "guias_remision")
public class GuiaRemisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_guia", nullable = false)
    private String tipoGuia;

    @Column(nullable = false)
    private String serie;

    @Column(nullable = false)
    private Integer numero;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "comprobante_id")
    private Long comprobanteId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "chofer_id", nullable = false)
    private Long choferId;

    @Column(name = "vehiculo_id", nullable = false)
    private Long vehiculoId;

    @Column(name = "motivo_traslado", nullable = false)
    private String motivoTraslado;

    @Column(name = "peso_total", nullable = false)
    private BigDecimal pesoTotal;

    @Column(name = "sunat_response_code")
    private String sunatResponseCode;

    @Column(name = "sunat_description", length = 1000)
    private String sunatDescription;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();
}

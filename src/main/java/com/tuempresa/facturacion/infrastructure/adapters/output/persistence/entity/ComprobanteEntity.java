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
}

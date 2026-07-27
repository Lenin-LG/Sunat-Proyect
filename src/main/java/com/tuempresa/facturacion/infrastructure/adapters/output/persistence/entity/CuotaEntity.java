package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "cuotas_pago")
public class CuotaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id", nullable = false)
    private ComprobanteEntity comprobante;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
}

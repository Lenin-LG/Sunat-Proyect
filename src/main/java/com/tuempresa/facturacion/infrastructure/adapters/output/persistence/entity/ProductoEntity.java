package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "productos")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;

    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "tipo_afectacion_igv_id", nullable = false)
    private String tipoAfectacionIgvId;

    @Column(name = "unidad_medida_id", nullable = false)
    private String unidadMedidaId;

    @Column(name = "stock_actual", nullable = false)
    private BigDecimal stockActual;

    @Column(name = "precio_costo_promedio", nullable = false)
    private BigDecimal precioCostoPromedio;

    @Column(name = "categoria_id")
    private Long categoriaId;
}

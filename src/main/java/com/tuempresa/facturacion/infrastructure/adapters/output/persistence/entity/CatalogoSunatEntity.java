package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "catalogos_sunat", uniqueConstraints = {
    @UniqueConstraint(name = "uq_catalogo_elemento", columnNames = {"catalogo_codigo", "elemento_codigo"})
})
public class CatalogoSunatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "catalogo_codigo", nullable = false, length = 3)
    private String catalogoCodigo;

    @Column(name = "elemento_codigo", nullable = false, length = 10)
    private String elementoCodigo;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(length = 50)
    private String abreviatura;
}

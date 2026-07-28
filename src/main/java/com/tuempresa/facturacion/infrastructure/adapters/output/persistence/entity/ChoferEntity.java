package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "choferes")
public class ChoferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_documento", nullable = false)
    private String tipoDocumento;

    @Column(name = "numero_documento", nullable = false, unique = true)
    private String numeroDocumento;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "licencia_conducir", nullable = false, unique = true)
    private String licenciaConducir;
}

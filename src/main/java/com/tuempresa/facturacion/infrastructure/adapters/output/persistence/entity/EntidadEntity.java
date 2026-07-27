package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "entidades")
public class EntidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_entidad_id", nullable = false)
    private String tipoEntidadId;

    @Column(name = "numero_documento", nullable = false, unique = true)
    private String numeroDocumento;

    @Column(name = "nombre_razon_social", nullable = false)
    private String nombreRazonSocial;

    private String direccion;
    private String correo;
}

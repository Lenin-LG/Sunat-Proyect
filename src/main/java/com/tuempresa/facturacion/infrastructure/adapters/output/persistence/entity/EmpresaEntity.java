package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empresa")
@Getter
@Setter
@NoArgsConstructor
public class EmpresaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 11)
    private String ruc;

    @Column(nullable = false)
    private String razonSocial;

    private String nombreComercial;

    @Column(nullable = false, length = 6)
    private String ubigeo;

    @Column(nullable = false)
    private String departamento;

    @Column(nullable = false)
    private String provincia;

    @Column(nullable = false)
    private String distrito;

    @Column(nullable = false)
    private String direccionFiscal;
}

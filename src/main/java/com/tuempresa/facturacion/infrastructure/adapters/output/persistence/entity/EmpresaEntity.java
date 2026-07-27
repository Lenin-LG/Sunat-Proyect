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

    @Column(name = "usuario_sol_produccion")
    private String usuarioSolProduccion;

    @Column(name = "password_sol_produccion")
    private String passwordSolProduccion;

    @Column(name = "modo_produccion")
    private boolean modoProduccion;

    @Column(name = "certificado_base64")
    private String certificadoBase64;

    @Column(name = "certificado_password")
    private String certificadoPassword;
}

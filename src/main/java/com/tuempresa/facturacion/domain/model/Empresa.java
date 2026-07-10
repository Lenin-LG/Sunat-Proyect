package com.tuempresa.facturacion.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Empresa {
    private Long id;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String ubigeo;
    private String departamento;
    private String provincia;
    private String distrito;
    private String direccionFiscal;
}

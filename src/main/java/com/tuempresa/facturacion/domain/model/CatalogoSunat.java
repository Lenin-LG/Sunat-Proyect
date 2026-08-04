package com.tuempresa.facturacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogoSunat {
    private Long id;
    private String catalogoCodigo;
    private String elementoCodigo;
    private String descripcion;
    private String abreviatura;
}

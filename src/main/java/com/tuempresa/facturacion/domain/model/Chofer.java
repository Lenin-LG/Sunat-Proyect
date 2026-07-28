package com.tuempresa.facturacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chofer {
    private Long id;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombre;
    private String licenciaConducir;
}

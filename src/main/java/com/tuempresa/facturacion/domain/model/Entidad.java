package com.tuempresa.facturacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entidad {
    private Long id;
    private String tipoEntidadId; // '1' = DNI, '6' = RUC
    private String numeroDocumento;
    private String nombreRazonSocial;
    private String direccion;
    private String correo;
}

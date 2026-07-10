package com.tuempresa.facturacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaSunat {
    private boolean aceptado;
    private String responseCode;
    private String description;
    private List<String> notes;

    public static RespuestaSunat rechazadoPorError(String mensaje) {
        return new RespuestaSunat(false, null, mensaje, List.of());
    }
}

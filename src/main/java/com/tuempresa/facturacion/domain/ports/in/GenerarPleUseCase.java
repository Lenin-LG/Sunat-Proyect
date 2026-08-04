package com.tuempresa.facturacion.domain.ports.in;

import com.tuempresa.facturacion.domain.model.PleFile;

public interface GenerarPleUseCase {
    PleFile generarVentas(int mes, int anio);
    PleFile generarCompras(int mes, int anio);
}

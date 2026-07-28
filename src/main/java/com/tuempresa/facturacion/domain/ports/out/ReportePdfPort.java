package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.model.Empresa;

public interface ReportePdfPort {
    byte[] generarFacturaPdf(Comprobante comprobante, Empresa empresa);
}

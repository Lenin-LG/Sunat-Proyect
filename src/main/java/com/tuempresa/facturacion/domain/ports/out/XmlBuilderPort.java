package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.model.Empresa;
import org.w3c.dom.Document;

public interface XmlBuilderPort {
    Document construir(Comprobante comprobante, Empresa empresa);
}

package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.RespuestaSunat;
import org.w3c.dom.Document;

public interface SunatGuiaSoapPort {
    RespuestaSunat enviarGuia(String nombreArchivoSinExtension, Document xmlFirmado);
}

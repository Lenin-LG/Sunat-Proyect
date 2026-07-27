package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Entidad;
import java.util.Optional;

public interface ConsultaDocumentoPort {
    Optional<Entidad> consultarRuc(String ruc);
    Optional<Entidad> consultarDni(String dni);
}

package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Comprobante;
import java.util.Optional;

public interface ComprobantePersistencePort {
    Comprobante save(Comprobante comprobante);
    Optional<Comprobante> findById(Long id);
    Optional<Comprobante> findTopByTipoDocumentoAndSerieOrderByNumeroDesc(String tipoDocumento, String serie);
}

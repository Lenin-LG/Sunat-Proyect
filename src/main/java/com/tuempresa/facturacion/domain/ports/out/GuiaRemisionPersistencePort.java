package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.GuiaRemision;
import java.util.Optional;

public interface GuiaRemisionPersistencePort {
    GuiaRemision save(GuiaRemision guia);
    Optional<GuiaRemision> findById(Long id);
    Optional<GuiaRemision> findTopByTipoGuiaAndSerieOrderByNumeroDesc(String tipoGuia, String serie);
}

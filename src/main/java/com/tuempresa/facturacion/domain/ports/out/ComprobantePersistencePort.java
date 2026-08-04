package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Comprobante;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ComprobantePersistencePort {
    Comprobante save(Comprobante comprobante);
    Optional<Comprobante> findById(Long id);
    Optional<Comprobante> findTopByTipoDocumentoAndSerieOrderByNumeroDesc(String tipoDocumento, String serie);
    List<Comprobante> findByFechaEmisionBetween(LocalDate startDate, LocalDate endDate);
}

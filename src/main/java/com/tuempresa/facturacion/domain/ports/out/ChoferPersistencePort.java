package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Chofer;
import java.util.List;
import java.util.Optional;

public interface ChoferPersistencePort {
    Chofer save(Chofer chofer);
    Optional<Chofer> findById(Long id);
    Optional<Chofer> findByNumeroDocumento(String numeroDocumento);
    List<Chofer> findAll();
}

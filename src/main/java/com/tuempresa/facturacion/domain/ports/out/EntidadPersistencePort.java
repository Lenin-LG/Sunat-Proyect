package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Entidad;
import java.util.List;
import java.util.Optional;

public interface EntidadPersistencePort {
    Entidad save(Entidad entidad);
    Optional<Entidad> findById(Long id);
    Optional<Entidad> findByNumeroDocumento(String numeroDocumento);
    List<Entidad> findAll();
    void deleteById(Long id);
}

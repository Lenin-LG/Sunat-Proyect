package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.CatalogoSunat;
import java.util.Optional;

public interface CatalogoSunatPersistencePort {
    Optional<CatalogoSunat> findByCatalogoCodigoAndElementoCodigo(String catalogoCodigo, String elementoCodigo);
}

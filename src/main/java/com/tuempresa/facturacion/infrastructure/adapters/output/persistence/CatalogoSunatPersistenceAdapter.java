package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.CatalogoSunat;
import com.tuempresa.facturacion.domain.ports.out.CatalogoSunatPersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.CatalogoSunatMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.CatalogoSunatJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class CatalogoSunatPersistenceAdapter implements CatalogoSunatPersistencePort {

    private final CatalogoSunatJpaRepository repository;

    public CatalogoSunatPersistenceAdapter(CatalogoSunatJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<CatalogoSunat> findByCatalogoCodigoAndElementoCodigo(String catalogoCodigo, String elementoCodigo) {
        return repository.findByCatalogoCodigoAndElementoCodigo(catalogoCodigo, elementoCodigo)
                .map(CatalogoSunatMapper::toDomain);
    }
}

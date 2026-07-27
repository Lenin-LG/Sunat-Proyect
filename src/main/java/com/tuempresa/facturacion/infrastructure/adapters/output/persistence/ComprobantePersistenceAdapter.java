package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.ports.out.ComprobantePersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ComprobanteEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.ComprobanteMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.ComprobanteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ComprobantePersistenceAdapter implements ComprobantePersistencePort {

    private final ComprobanteJpaRepository repository;

    public ComprobantePersistenceAdapter(ComprobanteJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Comprobante save(Comprobante comprobante) {
        ComprobanteEntity entity = ComprobanteMapper.toEntity(comprobante);
        entity = repository.save(entity);
        return ComprobanteMapper.toDomain(entity);
    }

    @Override
    public Optional<Comprobante> findById(Long id) {
        return repository.findById(id).map(ComprobanteMapper::toDomain);
    }

    @Override
    public Optional<Comprobante> findTopByTipoDocumentoAndSerieOrderByNumeroDesc(String tipoDocumento, String serie) {
        return repository.findTopByTipoDocumentoAndSerieOrderByNumeroDesc(tipoDocumento, serie)
                .map(ComprobanteMapper::toDomain);
    }
}

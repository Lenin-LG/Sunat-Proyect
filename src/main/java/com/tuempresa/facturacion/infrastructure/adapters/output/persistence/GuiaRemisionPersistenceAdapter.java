package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.GuiaRemision;
import com.tuempresa.facturacion.domain.ports.out.GuiaRemisionPersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.GuiaRemisionEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.GuiaRemisionMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.GuiaRemisionJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class GuiaRemisionPersistenceAdapter implements GuiaRemisionPersistencePort {

    private final GuiaRemisionJpaRepository repository;

    public GuiaRemisionPersistenceAdapter(GuiaRemisionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public GuiaRemision save(GuiaRemision de) {
        GuiaRemisionEntity entity = GuiaRemisionMapper.toEntity(de);
        return GuiaRemisionMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<GuiaRemision> findById(Long id) {
        return repository.findById(id).map(GuiaRemisionMapper::toDomain);
    }

    @Override
    public Optional<GuiaRemision> findTopByTipoGuiaAndSerieOrderByNumeroDesc(String tipoGuia, String serie) {
        return repository.findTopByTipoGuiaAndSerieOrderByNumeroDesc(tipoGuia, serie)
                .map(GuiaRemisionMapper::toDomain);
    }
}

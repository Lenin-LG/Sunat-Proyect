package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.Kardex;
import com.tuempresa.facturacion.domain.ports.out.KardexPersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.KardexEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.KardexMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.KardexJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KardexPersistenceAdapter implements KardexPersistencePort {

    private final KardexJpaRepository repository;

    public KardexPersistenceAdapter(KardexJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Kardex save(Kardex kardex) {
        KardexEntity entity = KardexMapper.toEntity(kardex);
        return KardexMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<Kardex> findByProductoId(Long productoId) {
        return repository.findByProductoId(productoId).stream()
                .map(KardexMapper::toDomain)
                .collect(Collectors.toList());
    }
}

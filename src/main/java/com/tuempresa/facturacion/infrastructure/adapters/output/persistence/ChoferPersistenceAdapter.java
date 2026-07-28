package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.Chofer;
import com.tuempresa.facturacion.domain.ports.out.ChoferPersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ChoferEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.ChoferMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.ChoferJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ChoferPersistenceAdapter implements ChoferPersistencePort {

    private final ChoferJpaRepository repository;

    public ChoferPersistenceAdapter(ChoferJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Chofer save(Chofer chofer) {
        ChoferEntity entity = ChoferMapper.toEntity(chofer);
        return ChoferMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Chofer> findById(Long id) {
        return repository.findById(id).map(ChoferMapper::toDomain);
    }

    @Override
    public Optional<Chofer> findByNumeroDocumento(String numeroDocumento) {
        return repository.findByNumeroDocumento(numeroDocumento).map(ChoferMapper::toDomain);
    }

    @Override
    public List<Chofer> findAll() {
        return repository.findAll().stream()
                .map(ChoferMapper::toDomain)
                .collect(Collectors.toList());
    }
}

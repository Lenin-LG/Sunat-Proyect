package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.Entidad;
import com.tuempresa.facturacion.domain.ports.out.EntidadPersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.EntidadEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.EntidadMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.EntidadJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EntidadPersistenceAdapter implements EntidadPersistencePort {

    private final EntidadJpaRepository repository;

    public EntidadPersistenceAdapter(EntidadJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Entidad save(Entidad entidad) {
        EntidadEntity entity = EntidadMapper.toEntity(entidad);
        return EntidadMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Entidad> findById(Long id) {
        return repository.findById(id).map(EntidadMapper::toDomain);
    }

    @Override
    public Optional<Entidad> findByNumeroDocumento(String numeroDocumento) {
        return repository.findByNumeroDocumento(numeroDocumento).map(EntidadMapper::toDomain);
    }

    @Override
    public List<Entidad> findAll() {
        return repository.findAll().stream()
                .map(EntidadMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

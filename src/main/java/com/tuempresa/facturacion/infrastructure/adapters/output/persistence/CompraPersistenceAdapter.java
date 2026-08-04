package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.Compra;
import com.tuempresa.facturacion.domain.ports.out.CompraPersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.CompraEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.CompraMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.CompraJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CompraPersistenceAdapter implements CompraPersistencePort {

    private final CompraJpaRepository repository;

    public CompraPersistenceAdapter(CompraJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Compra save(Compra compra) {
        CompraEntity entity = CompraMapper.toEntity(compra);
        CompraEntity saved = repository.save(entity);
        return CompraMapper.toDomain(saved);
    }

    @Override
    public Optional<Compra> findById(Long id) {
        return repository.findById(id).map(CompraMapper::toDomain);
    }

    @Override
    public List<Compra> findAll() {
        return repository.findAll().stream()
                .map(CompraMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Compra> findByFechaEmisionBetween(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return repository.findByFechaEmisionBetween(startDate, endDate).stream()
                .map(CompraMapper::toDomain)
                .collect(Collectors.toList());
    }
}

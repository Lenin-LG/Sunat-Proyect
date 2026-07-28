package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.Vehiculo;
import com.tuempresa.facturacion.domain.ports.out.VehiculoPersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.VehiculoEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.VehiculoMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.VehiculoJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class VehiculoPersistenceAdapter implements VehiculoPersistencePort {

    private final VehiculoJpaRepository repository;

    public VehiculoPersistenceAdapter(VehiculoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vehiculo save(Vehiculo vehiculo) {
        VehiculoEntity entity = VehiculoMapper.toEntity(vehiculo);
        return VehiculoMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Vehiculo> findById(Long id) {
        return repository.findById(id).map(VehiculoMapper::toDomain);
    }

    @Override
    public Optional<Vehiculo> findByPlaca(String placa) {
        return repository.findByPlaca(placa).map(VehiculoMapper::toDomain);
    }

    @Override
    public List<Vehiculo> findAll() {
        return repository.findAll().stream()
                .map(VehiculoMapper::toDomain)
                .collect(Collectors.toList());
    }
}

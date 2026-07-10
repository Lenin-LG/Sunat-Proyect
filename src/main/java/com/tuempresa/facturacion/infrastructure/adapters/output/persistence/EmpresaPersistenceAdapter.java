package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.Empresa;
import com.tuempresa.facturacion.domain.ports.out.EmpresaPersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.EmpresaEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.EmpresaMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.EmpresaJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class EmpresaPersistenceAdapter implements EmpresaPersistencePort {

    private final EmpresaJpaRepository repository;

    public EmpresaPersistenceAdapter(EmpresaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Empresa findByRuc(String ruc) {
        EmpresaEntity entity = repository.findByRuc(ruc);
        return EmpresaMapper.toDomain(entity);
    }

    @Override
    public Empresa save(Empresa empresa) {
        EmpresaEntity entity = EmpresaMapper.toEntity(empresa);
        entity = repository.save(entity);
        return EmpresaMapper.toDomain(entity);
    }
}

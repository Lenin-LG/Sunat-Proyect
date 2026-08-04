package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.CobroPago;
import com.tuempresa.facturacion.domain.ports.out.CobroPagoPersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.CobroPagoEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.CobroPagoMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.CobroPagoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CobroPagoPersistenceAdapter implements CobroPagoPersistencePort {

    private final CobroPagoJpaRepository repository;

    public CobroPagoPersistenceAdapter(CobroPagoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public CobroPago save(CobroPago cobroPago) {
        CobroPagoEntity entity = CobroPagoMapper.toEntity(cobroPago);
        CobroPagoEntity saved = repository.save(entity);
        return CobroPagoMapper.toDomain(saved);
    }

    @Override
    public List<CobroPago> findByComprobanteId(Long comprobanteId) {
        return repository.findByComprobanteId(comprobanteId).stream()
                .map(CobroPagoMapper::toDomain)
                .collect(Collectors.toList());
    }
}

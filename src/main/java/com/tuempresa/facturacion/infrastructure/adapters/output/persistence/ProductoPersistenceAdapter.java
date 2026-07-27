package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.Producto;
import com.tuempresa.facturacion.domain.ports.out.ProductoPersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ProductoEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.ProductoMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.ProductoJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductoPersistenceAdapter implements ProductoPersistencePort {

    private final ProductoJpaRepository repository;

    public ProductoPersistenceAdapter(ProductoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Producto save(Producto producto) {
        ProductoEntity entity = ProductoMapper.toEntity(producto);
        return ProductoMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return repository.findById(id).map(ProductoMapper::toDomain);
    }

    @Override
    public Optional<Producto> findByCodigo(String codigo) {
        return repository.findByCodigo(codigo).map(ProductoMapper::toDomain);
    }

    @Override
    public List<Producto> findAll() {
        return repository.findAll().stream()
                .map(ProductoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

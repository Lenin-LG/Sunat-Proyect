package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoPersistencePort {
    Producto save(Producto producto);
    Optional<Producto> findById(Long id);
    Optional<Producto> findByCodigo(String codigo);
    List<Producto> findAll();
    void deleteById(Long id);
}

package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Kardex;
import java.util.List;

public interface KardexPersistencePort {
    Kardex save(Kardex kardex);
    List<Kardex> findByProductoId(Long productoId);
}

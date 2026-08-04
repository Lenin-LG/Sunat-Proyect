package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.CobroPago;
import java.util.List;

public interface CobroPagoPersistencePort {
    CobroPago save(CobroPago cobroPago);
    List<CobroPago> findByComprobanteId(Long comprobanteId);
}

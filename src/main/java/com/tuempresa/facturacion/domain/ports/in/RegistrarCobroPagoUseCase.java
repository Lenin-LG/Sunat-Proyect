package com.tuempresa.facturacion.domain.ports.in;

import com.tuempresa.facturacion.domain.model.CobroPago;
import com.tuempresa.facturacion.domain.ports.in.dto.CobroPagoCommand;
import java.util.List;

public interface RegistrarCobroPagoUseCase {
    CobroPago registrar(CobroPagoCommand command);
    List<CobroPago> listarPorComprobante(Long comprobanteId);
}

package com.tuempresa.facturacion.domain.ports.in;

import com.tuempresa.facturacion.domain.model.Comprobante;

public interface AnularComprobanteUseCase {
    Comprobante anular(Long id, String motivo);
    Comprobante consultarEstadoTicket(Long id);
}

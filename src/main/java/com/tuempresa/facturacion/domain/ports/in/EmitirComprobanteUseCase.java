package com.tuempresa.facturacion.domain.ports.in;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.ports.in.dto.ComprobanteCommand;

public interface EmitirComprobanteUseCase {
    Comprobante emitir(ComprobanteCommand command);
}

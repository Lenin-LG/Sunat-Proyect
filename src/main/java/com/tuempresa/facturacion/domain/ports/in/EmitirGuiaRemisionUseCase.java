package com.tuempresa.facturacion.domain.ports.in;

import com.tuempresa.facturacion.domain.model.GuiaRemision;
import com.tuempresa.facturacion.domain.ports.in.dto.GuiaCommand;

public interface EmitirGuiaRemisionUseCase {
    GuiaRemision emitir(GuiaCommand command);
}

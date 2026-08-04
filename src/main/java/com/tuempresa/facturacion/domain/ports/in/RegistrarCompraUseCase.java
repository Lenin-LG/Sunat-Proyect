package com.tuempresa.facturacion.domain.ports.in;

import com.tuempresa.facturacion.domain.model.Compra;
import com.tuempresa.facturacion.domain.ports.in.dto.CompraCommand;
import java.util.List;

public interface RegistrarCompraUseCase {
    Compra registrar(CompraCommand command);
    List<Compra> listar();
}

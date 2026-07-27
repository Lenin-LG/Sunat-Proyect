package com.tuempresa.facturacion.domain.ports.in;

import com.tuempresa.facturacion.domain.model.Empresa;

public interface AdministrarEmpresaUseCase {
    Empresa registrarOActualizar(Empresa empresa);
    Empresa obtenerPorRuc(String ruc);
}

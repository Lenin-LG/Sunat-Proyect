package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Empresa;

public interface EmpresaPersistencePort {
    Empresa findByRuc(String ruc);
    Empresa save(Empresa empresa);
}

package com.tuempresa.facturacion.domain.ports.in;

import com.tuempresa.facturacion.domain.model.Entidad;
import java.util.List;

public interface AdministrarClienteUseCase {
    Entidad registrar(Entidad entidad);
    Entidad actualizar(Long id, Entidad entidad);
    Entidad obtener(Long id);
    List<Entidad> listar();
    void eliminar(Long id);
    Entidad buscarPorDocumentoAuto(String tipoDoc, String numeroDoc);
}

package com.tuempresa.facturacion.domain.ports.in;

import com.tuempresa.facturacion.domain.model.Usuario;

public interface AdministrarUsuarioUseCase {
    Usuario registrar(Usuario usuario);
    Usuario obtenerPorUsername(String username);
}

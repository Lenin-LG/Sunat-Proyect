package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioPersistencePort {
    Usuario save(Usuario usuario);
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findById(Long id);
}

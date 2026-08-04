package com.tuempresa.facturacion.infrastructure.adapters.output.persistence;

import com.tuempresa.facturacion.domain.model.Usuario;
import com.tuempresa.facturacion.domain.ports.out.UsuarioPersistencePort;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper.UsuarioMapper;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class UsuarioPersistenceAdapter implements UsuarioPersistencePort {

    private final UsuarioJpaRepository repository;

    public UsuarioPersistenceAdapter(UsuarioJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = UsuarioMapper.toEntity(usuario);
        UsuarioEntity saved = repository.save(entity);
        return UsuarioMapper.toDomain(saved);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return repository.findByUsername(username).map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return repository.findByEmail(email).map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return repository.findById(id).map(UsuarioMapper::toDomain);
    }
}

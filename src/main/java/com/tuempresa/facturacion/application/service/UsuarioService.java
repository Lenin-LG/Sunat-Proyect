package com.tuempresa.facturacion.application.service;

import com.tuempresa.facturacion.domain.model.Usuario;
import com.tuempresa.facturacion.domain.ports.in.AdministrarUsuarioUseCase;
import com.tuempresa.facturacion.domain.ports.out.PasswordEncoderPort;
import com.tuempresa.facturacion.domain.ports.out.UsuarioPersistencePort;

public class UsuarioService implements AdministrarUsuarioUseCase {

    private final UsuarioPersistencePort usuarioPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;

    public UsuarioService(UsuarioPersistencePort usuarioPersistencePort, PasswordEncoderPort passwordEncoderPort) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public Usuario registrar(Usuario usuario) {
        if (usuarioPersistencePort.findByUsername(usuario.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado");
        }
        if (usuarioPersistencePort.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }
        usuario.setPassword(passwordEncoderPort.encode(usuario.getPassword()));
        return usuarioPersistencePort.save(usuario);
    }

    @Override
    public Usuario obtenerPorUsername(String username) {
        return usuarioPersistencePort.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
    }
}

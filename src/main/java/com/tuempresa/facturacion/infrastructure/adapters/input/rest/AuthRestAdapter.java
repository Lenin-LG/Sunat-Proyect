package com.tuempresa.facturacion.infrastructure.adapters.input.rest;

import com.tuempresa.facturacion.domain.model.Usuario;
import com.tuempresa.facturacion.domain.ports.in.AdministrarUsuarioUseCase;
import com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto.AuthResponse;
import com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto.LoginRequest;
import com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto.RegistroRequest;
import com.tuempresa.facturacion.infrastructure.config.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRestAdapter {

    private final AuthenticationManager authenticationManager;
    private final AdministrarUsuarioUseCase usuarioUseCase;
    private final JwtTokenProvider tokenProvider;

    public AuthRestAdapter(AuthenticationManager authenticationManager,
                           AdministrarUsuarioUseCase usuarioUseCase,
                           JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.usuarioUseCase = usuarioUseCase;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse("Bearer", jwt));
    }

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody RegistroRequest request) {
        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .rol(request.getRol().toUpperCase())
                .build();

        Usuario registrado = usuarioUseCase.registrar(usuario);
        return ResponseEntity.ok(registrado);
    }
}

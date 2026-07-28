package com.tuempresa.facturacion.infrastructure.adapters.input.rest;

import com.tuempresa.facturacion.domain.model.GuiaRemision;
import com.tuempresa.facturacion.domain.ports.in.EmitirGuiaRemisionUseCase;
import com.tuempresa.facturacion.domain.ports.in.dto.GuiaCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guias")
public class GuiaRestAdapter {

    private final EmitirGuiaRemisionUseCase emitirGuiaUseCase;

    public GuiaRestAdapter(EmitirGuiaRemisionUseCase emitirGuiaUseCase) {
        this.emitirGuiaUseCase = emitirGuiaUseCase;
    }

    @PostMapping
    public ResponseEntity<GuiaRemision> emitir(@RequestBody GuiaCommand command) {
        return ResponseEntity.ok(emitirGuiaUseCase.emitir(command));
    }
}

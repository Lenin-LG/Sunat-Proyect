package com.tuempresa.facturacion.infrastructure.adapters.input.rest;

import com.tuempresa.facturacion.domain.model.Entidad;
import com.tuempresa.facturacion.domain.ports.in.AdministrarClienteUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteRestAdapter {

    private final AdministrarClienteUseCase clienteUseCase;

    public ClienteRestAdapter(AdministrarClienteUseCase clienteUseCase) {
        this.clienteUseCase = clienteUseCase;
    }

    @PostMapping
    public ResponseEntity<Entidad> registrar(@RequestBody Entidad entidad) {
        return ResponseEntity.ok(clienteUseCase.registrar(entidad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entidad> actualizar(@PathVariable Long id, @RequestBody Entidad entidad) {
        return ResponseEntity.ok(clienteUseCase.actualizar(id, entidad));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entidad> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(clienteUseCase.obtener(id));
    }

    @GetMapping
    public ResponseEntity<List<Entidad>> listar() {
        return ResponseEntity.ok(clienteUseCase.listar());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar-auto")
    public ResponseEntity<Entidad> buscarAuto(@RequestParam String tipoDoc, @RequestParam String numeroDoc) {
        return ResponseEntity.ok(clienteUseCase.buscarPorDocumentoAuto(tipoDoc, numeroDoc));
    }
}

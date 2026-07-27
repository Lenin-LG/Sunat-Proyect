package com.tuempresa.facturacion.infrastructure.adapters.input.rest;

import com.tuempresa.facturacion.domain.model.Empresa;
import com.tuempresa.facturacion.domain.ports.in.AdministrarEmpresaUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaRestAdapter {

    private final AdministrarEmpresaUseCase empresaUseCase;

    public EmpresaRestAdapter(AdministrarEmpresaUseCase empresaUseCase) {
        this.empresaUseCase = empresaUseCase;
    }

    @PutMapping("/config")
    public ResponseEntity<Empresa> configurar(@RequestBody Empresa empresa) {
        return ResponseEntity.ok(empresaUseCase.registrarOActualizar(empresa));
    }

    @GetMapping("/{ruc}")
    public ResponseEntity<Empresa> obtener(@PathVariable String ruc) {
        return ResponseEntity.ok(empresaUseCase.obtenerPorRuc(ruc));
    }
}

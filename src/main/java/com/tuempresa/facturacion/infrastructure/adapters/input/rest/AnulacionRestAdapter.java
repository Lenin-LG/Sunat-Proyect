package com.tuempresa.facturacion.infrastructure.adapters.input.rest;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.ports.in.AnularComprobanteUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comprobantes")
public class AnulacionRestAdapter {

    private final AnularComprobanteUseCase anulacionUseCase;

    public AnulacionRestAdapter(AnularComprobanteUseCase anulacionUseCase) {
        this.anulacionUseCase = anulacionUseCase;
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<Comprobante> anular(@PathVariable Long id, @RequestParam String motivo) {
        return ResponseEntity.ok(anulacionUseCase.anular(id, motivo));
    }

    @GetMapping("/{id}/consultar-ticket")
    public ResponseEntity<Comprobante> consultarTicket(@PathVariable Long id) {
        return ResponseEntity.ok(anulacionUseCase.consultarEstadoTicket(id));
    }
}

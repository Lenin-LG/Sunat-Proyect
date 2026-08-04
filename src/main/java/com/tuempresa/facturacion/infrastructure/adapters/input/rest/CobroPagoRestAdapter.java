package com.tuempresa.facturacion.infrastructure.adapters.input.rest;

import com.tuempresa.facturacion.domain.model.CobroPago;
import com.tuempresa.facturacion.domain.ports.in.RegistrarCobroPagoUseCase;
import com.tuempresa.facturacion.domain.ports.in.dto.CobroPagoCommand;
import com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto.CobroPagoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cobros-pagos")
public class CobroPagoRestAdapter {

    private final RegistrarCobroPagoUseCase useCase;

    public CobroPagoRestAdapter(RegistrarCobroPagoUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<CobroPago> registrar(@RequestBody CobroPagoRequest request) {
        CobroPagoCommand command = CobroPagoCommand.builder()
                .comprobanteId(request.getComprobanteId())
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .fechaPago(request.getFechaPago())
                .referencia(request.getReferencia())
                .build();

        return ResponseEntity.ok(useCase.registrar(command));
    }

    @GetMapping("/comprobante/{comprobanteId}")
    public ResponseEntity<List<CobroPago>> listarPorComprobante(@PathVariable Long comprobanteId) {
        return ResponseEntity.ok(useCase.listarPorComprobante(comprobanteId));
    }
}

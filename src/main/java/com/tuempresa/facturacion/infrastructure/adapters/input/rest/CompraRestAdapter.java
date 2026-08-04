package com.tuempresa.facturacion.infrastructure.adapters.input.rest;

import com.tuempresa.facturacion.domain.model.Compra;
import com.tuempresa.facturacion.domain.ports.in.RegistrarCompraUseCase;
import com.tuempresa.facturacion.domain.ports.in.dto.CompraCommand;
import com.tuempresa.facturacion.domain.ports.in.dto.CompraItemCommand;
import com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto.CompraRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/compras")
public class CompraRestAdapter {

    private final RegistrarCompraUseCase compraUseCase;

    public CompraRestAdapter(RegistrarCompraUseCase compraUseCase) {
        this.compraUseCase = compraUseCase;
    }

    @PostMapping
    public ResponseEntity<Compra> registrar(@RequestBody CompraRequest request) {
        CompraCommand command = CompraCommand.builder()
                .tipoDocumento(request.getTipoDocumento())
                .serie(request.getSerie())
                .numero(request.getNumero())
                .proveedorId(request.getProveedorId())
                .fechaEmision(request.getFechaEmision())
                .items(request.getItems().stream()
                        .map(item -> CompraItemCommand.builder()
                                .productoId(item.getProductoId())
                                .cantidad(item.getCantidad())
                                .precioUnitario(item.getPrecioUnitario())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(compraUseCase.registrar(command));
    }

    @GetMapping
    public ResponseEntity<List<Compra>> listar() {
        return ResponseEntity.ok(compraUseCase.listar());
    }
}

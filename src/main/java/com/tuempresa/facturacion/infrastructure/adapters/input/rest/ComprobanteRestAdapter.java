package com.tuempresa.facturacion.infrastructure.adapters.input.rest;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.ports.in.EmitirComprobanteUseCase;
import com.tuempresa.facturacion.domain.ports.in.dto.ComprobanteCommand;
import com.tuempresa.facturacion.domain.ports.in.dto.ItemCommand;
import com.tuempresa.facturacion.infrastructure.adapters.input.rest.dto.ComprobanteRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comprobantes")
public class ComprobanteRestAdapter {

    private final EmitirComprobanteUseCase emitirComprobanteUseCase;

    public ComprobanteRestAdapter(EmitirComprobanteUseCase emitirComprobanteUseCase) {
        this.emitirComprobanteUseCase = emitirComprobanteUseCase;
    }

    @PostMapping("/factura")
    @Transactional
    public ResponseEntity<Comprobante> emitirFactura(@Valid @RequestBody ComprobanteRequest request) {
        ComprobanteCommand command = toCommand(request, "01");
        return ResponseEntity.ok(emitirComprobanteUseCase.emitir(command));
    }

    @PostMapping("/boleta")
    @Transactional
    public ResponseEntity<Comprobante> emitirBoleta(@Valid @RequestBody ComprobanteRequest request) {
        ComprobanteCommand command = toCommand(request, "03");
        return ResponseEntity.ok(emitirComprobanteUseCase.emitir(command));
    }

    private ComprobanteCommand toCommand(ComprobanteRequest request, String tipoDocumento) {
        return ComprobanteCommand.builder()
                .tipoDocumento(tipoDocumento)
                .serie(request.getSerie())
                .clienteTipoDocumento(request.getClienteTipoDocumento())
                .clienteNumeroDocumento(request.getClienteNumeroDocumento())
                .clienteNombre(request.getClienteNombre())
                .items(request.getItems().stream()
                        .map(item -> ItemCommand.builder()
                                .descripcion(item.getDescripcion())
                                .cantidad(item.getCantidad())
                                .precioUnitario(item.getPrecioUnitario())
                                .codigoProductoSunat(item.getCodigoProductoSunat())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

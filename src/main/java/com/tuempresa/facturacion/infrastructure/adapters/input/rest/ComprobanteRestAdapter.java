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

    @PostMapping("/nota-credito")
    @Transactional
    public ResponseEntity<Comprobante> emitirNotaCredito(@Valid @RequestBody ComprobanteRequest request) {
        ComprobanteCommand command = toCommand(request, "07");
        return ResponseEntity.ok(emitirComprobanteUseCase.emitir(command));
    }

    @PostMapping("/nota-debito")
    @Transactional
    public ResponseEntity<Comprobante> emitirNotaDebito(@Valid @RequestBody ComprobanteRequest request) {
        ComprobanteCommand command = toCommand(request, "08");
        return ResponseEntity.ok(emitirComprobanteUseCase.emitir(command));
    }

    private ComprobanteCommand toCommand(ComprobanteRequest request, String tipoDocumento) {
        return ComprobanteCommand.builder()
                .tipoDocumento(tipoDocumento)
                .serie(request.getSerie())
                .clienteTipoDocumento(request.getClienteTipoDocumento())
                .clienteNumeroDocumento(request.getClienteNumeroDocumento())
                .clienteNombre(request.getClienteNombre())
                .formaPago(request.getFormaPago())
                .detraccionCodigo(request.getDetraccionCodigo())
                .detraccionPorcentaje(request.getDetraccionPorcentaje())
                .detraccionMonto(request.getDetraccionMonto())
                .descuentoGlobal(request.getDescuentoGlobal())
                .totalImpuestoBolsa(request.getTotalImpuestoBolsa())
                .anticipoReferencia(request.getAnticipoReferencia())
                .saldoPendiente(request.getSaldoPendiente())
                .cuotas(request.getCuotas() == null ? null : request.getCuotas().stream()
                        .map(c -> ComprobanteCommand.CuotaCommand.builder()
                                .numeroCuota(c.getNumeroCuota())
                                .monto(c.getMonto())
                                .fechaVencimiento(c.getFechaVencimiento() != null ? java.time.LocalDate.parse(c.getFechaVencimiento()) : null)
                                .build())
                        .collect(Collectors.toList()))
                .documentoModificadoId(request.getDocumentoModificadoId())
                .documentoModificadoTipo(request.getDocumentoModificadoTipo())
                .notaMotivoCodigo(request.getNotaMotivoCodigo())
                .notaMotivoDescripcion(request.getNotaMotivoDescripcion())
                .items(request.getItems().stream()
                        .map(item -> ItemCommand.builder()
                                .descripcion(item.getDescripcion())
                                .cantidad(item.getCantidad())
                                .precioUnitario(item.getPrecioUnitario())
                                .codigoProductoSunat(item.getCodigoProductoSunat())
                                .codigoInterno(item.getCodigoInterno())
                                .tipoUnidad(item.getTipoUnidad())
                                .tipoAfectacionIgv(item.getTipoAfectacionIgv())
                                .impuestoBolsa(item.getImpuestoBolsa())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

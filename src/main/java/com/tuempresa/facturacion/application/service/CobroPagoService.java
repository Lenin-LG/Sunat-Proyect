package com.tuempresa.facturacion.application.service;

import com.tuempresa.facturacion.domain.model.CobroPago;
import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.ports.in.RegistrarCobroPagoUseCase;
import com.tuempresa.facturacion.domain.ports.in.dto.CobroPagoCommand;
import com.tuempresa.facturacion.domain.ports.out.CobroPagoPersistencePort;
import com.tuempresa.facturacion.domain.ports.out.ComprobantePersistencePort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CobroPagoService implements RegistrarCobroPagoUseCase {

    private final CobroPagoPersistencePort cobroPagoPersistencePort;
    private final ComprobantePersistencePort comprobantePersistencePort;

    public CobroPagoService(CobroPagoPersistencePort cobroPagoPersistencePort, ComprobantePersistencePort comprobantePersistencePort) {
        this.cobroPagoPersistencePort = cobroPagoPersistencePort;
        this.comprobantePersistencePort = comprobantePersistencePort;
    }

    @Override
    @Transactional
    public CobroPago registrar(CobroPagoCommand command) {
        Comprobante comprobante = comprobantePersistencePort.findById(command.getComprobanteId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el comprobante con ID: " + command.getComprobanteId()));

        BigDecimal oldSaldo = comprobante.getSaldoPendiente() != null ? comprobante.getSaldoPendiente() : BigDecimal.ZERO;
        BigDecimal newSaldo = oldSaldo.subtract(command.getMonto());
        if (newSaldo.compareTo(BigDecimal.ZERO) < 0) {
            newSaldo = BigDecimal.ZERO;
        }
        comprobante.setSaldoPendiente(newSaldo);
        comprobantePersistencePort.save(comprobante);

        CobroPago cobroPago = CobroPago.builder()
                .comprobanteId(command.getComprobanteId())
                .monto(command.getMonto())
                .metodoPago(command.getMetodoPago())
                .fechaPago(command.getFechaPago() != null ? command.getFechaPago() : java.time.LocalDate.now())
                .referencia(command.getReferencia())
                .creadoEn(LocalDateTime.now())
                .build();

        return cobroPagoPersistencePort.save(cobroPago);
    }

    @Override
    public List<CobroPago> listarPorComprobante(Long comprobanteId) {
        return cobroPagoPersistencePort.findByComprobanteId(comprobanteId);
    }
}

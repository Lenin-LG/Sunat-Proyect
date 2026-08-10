package com.tuempresa.facturacion.application.service;

import com.tuempresa.facturacion.domain.model.Compra;
import com.tuempresa.facturacion.domain.model.CompraDetalle;
import com.tuempresa.facturacion.domain.ports.in.AdministrarProductoUseCase;
import com.tuempresa.facturacion.domain.ports.in.RegistrarCompraUseCase;
import com.tuempresa.facturacion.domain.ports.in.dto.CompraCommand;
import com.tuempresa.facturacion.domain.ports.in.dto.CompraItemCommand;
import com.tuempresa.facturacion.domain.ports.out.CompraPersistencePort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CompraService implements RegistrarCompraUseCase {

    private final CompraPersistencePort compraPersistencePort;
    private final AdministrarProductoUseCase productoUseCase;

    public CompraService(CompraPersistencePort compraPersistencePort, AdministrarProductoUseCase productoUseCase) {
        this.compraPersistencePort = compraPersistencePort;
        this.productoUseCase = productoUseCase;
    }

    @Override
    @Transactional
    public Compra registrar(CompraCommand command) {
        // Calcular totales
        BigDecimal totalGravada = BigDecimal.ZERO;
        BigDecimal totalIgv = BigDecimal.ZERO;
        BigDecimal totalPagar = BigDecimal.ZERO;

        for (CompraItemCommand item : command.getItems()) {
            BigDecimal precioConIgv = item.getPrecioUnitario();
            BigDecimal precioSinIgv = precioConIgv.divide(new BigDecimal("1.18"), 6, RoundingMode.HALF_UP);
            
            BigDecimal subtotal = item.getCantidad().multiply(precioSinIgv);
            // Cálculo del IGV al 18% para compras
            BigDecimal igvItem = subtotal.multiply(new BigDecimal("0.18"));
            BigDecimal totalItem = subtotal.add(igvItem);

            totalGravada = totalGravada.add(subtotal);
            totalIgv = totalIgv.add(igvItem);
            totalPagar = totalPagar.add(totalItem);
        }

        Compra compra = Compra.builder()
                .tipoDocumento(command.getTipoDocumento())
                .serie(command.getSerie())
                .numero(command.getNumero())
                .proveedorId(command.getProveedorId())
                .fechaEmision(command.getFechaEmision())
                .totalGravada(totalGravada)
                .totalIgv(totalIgv)
                .totalPagar(totalPagar)
                .creadoEn(LocalDateTime.now())
                .build();

        Compra compraGuardada = compraPersistencePort.save(compra);

        List<CompraDetalle> detalles = command.getItems().stream()
                .map(item -> {
                    BigDecimal precioConIgv = item.getPrecioUnitario();
                    BigDecimal precioSinIgv = precioConIgv.divide(new BigDecimal("1.18"), 6, RoundingMode.HALF_UP);
                    
                    // Actualizar el stock y el costo promedio ponderado del producto
                    productoUseCase.registrarIngresoStock(item.getProductoId(), item.getCantidad(),
                            precioSinIgv);

                    return CompraDetalle.builder()
                            .compraId(compraGuardada.getId())
                            .productoId(item.getProductoId())
                            .cantidad(item.getCantidad())
                            .precioUnitario(precioSinIgv)
                            .build();
                })
                .collect(Collectors.toList());

        compraGuardada.setDetalles(detalles);

        return compraPersistencePort.save(compraGuardada);
    }

    @Override
    public List<Compra> listar() {
        return compraPersistencePort.findAll();
    }
}

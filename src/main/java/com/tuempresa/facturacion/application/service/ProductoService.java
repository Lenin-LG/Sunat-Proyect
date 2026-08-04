package com.tuempresa.facturacion.application.service;

import com.tuempresa.facturacion.domain.model.Kardex;
import com.tuempresa.facturacion.domain.model.Producto;
import com.tuempresa.facturacion.domain.ports.in.AdministrarProductoUseCase;
import com.tuempresa.facturacion.domain.ports.out.KardexPersistencePort;
import com.tuempresa.facturacion.domain.ports.out.ProductoPersistencePort;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductoService implements AdministrarProductoUseCase {

    private final ProductoPersistencePort productoPersistencePort;
    private final KardexPersistencePort kardexPersistencePort;

    public ProductoService(ProductoPersistencePort productoPersistencePort, KardexPersistencePort kardexPersistencePort) {
        this.productoPersistencePort = productoPersistencePort;
        this.kardexPersistencePort = kardexPersistencePort;
    }

    @Override
    public Producto registrar(Producto producto) {
        if (producto.getStockActual() == null) {
            producto.setStockActual(BigDecimal.ZERO);
        }
        if (producto.getPrecioCostoPromedio() == null) {
            producto.setPrecioCostoPromedio(BigDecimal.ZERO);
        }
        return productoPersistencePort.save(producto);
    }

    @Override
    public Producto actualizar(Long id, Producto producto) {
        Producto existing = obtener(id);
        existing.setCodigo(producto.getCodigo());
        existing.setDescripcion(producto.getDescripcion());
        existing.setPrecioUnitario(producto.getPrecioUnitario());
        existing.setTipoAfectacionIgvId(producto.getTipoAfectacionIgvId());
        existing.setUnidadMedidaId(producto.getUnidadMedidaId());
        existing.setCategoriaId(producto.getCategoriaId());
        return productoPersistencePort.save(existing);
    }

    @Override
    public Producto obtener(Long id) {
        return productoPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el producto con id: " + id));
    }

    @Override
    public List<Producto> listar() {
        return productoPersistencePort.findAll();
    }

    @Override
    public void eliminar(Long id) {
        productoPersistencePort.deleteById(id);
    }

    @Override
    public Producto registrarIngresoStock(Long id, BigDecimal cantidad, BigDecimal precioCosto) {
        Producto producto = obtener(id);
        
        BigDecimal oldStock = producto.getStockActual() != null ? producto.getStockActual() : BigDecimal.ZERO;
        BigDecimal oldCost = producto.getPrecioCostoPromedio() != null ? producto.getPrecioCostoPromedio() : BigDecimal.ZERO;
        
        BigDecimal totalOldVal = oldStock.multiply(oldCost);
        BigDecimal totalNewVal = cantidad.multiply(precioCosto);
        BigDecimal totalVal = totalOldVal.add(totalNewVal);
        
        BigDecimal nuevoStock = oldStock.add(cantidad);
        BigDecimal nuevoCostoPromedio = BigDecimal.ZERO;
        if (nuevoStock.compareTo(BigDecimal.ZERO) > 0) {
            nuevoCostoPromedio = totalVal.divide(nuevoStock, 4, java.math.RoundingMode.HALF_UP);
        }
        
        producto.setStockActual(nuevoStock);
        producto.setPrecioCostoPromedio(nuevoCostoPromedio);

        kardexPersistencePort.save(Kardex.builder()
                .productoId(id)
                .tipoMovimiento("COMPRA")
                .cantidad(cantidad)
                .precioUnitario(precioCosto)
                .stockResultante(nuevoStock)
                .creadoEn(LocalDateTime.now())
                .build());

        return productoPersistencePort.save(producto);
    }

    @Override
    public Producto registrarSalidaStock(Long id, BigDecimal cantidad, BigDecimal precioVenta) {
        Producto producto = obtener(id);
        BigDecimal nuevoStock = producto.getStockActual().subtract(cantidad);
        producto.setStockActual(nuevoStock);

        kardexPersistencePort.save(Kardex.builder()
                .productoId(id)
                .tipoMovimiento("VENTA")
                .cantidad(cantidad)
                .precioUnitario(precioVenta)
                .stockResultante(nuevoStock)
                .creadoEn(LocalDateTime.now())
                .build());

        return productoPersistencePort.save(producto);
    }
}

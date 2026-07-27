package com.tuempresa.facturacion.domain.ports.in;

import com.tuempresa.facturacion.domain.model.Producto;
import java.math.BigDecimal;
import java.util.List;

public interface AdministrarProductoUseCase {
    Producto registrar(Producto producto);
    Producto actualizar(Long id, Producto producto);
    Producto obtener(Long id);
    List<Producto> listar();
    void eliminar(Long id);
    Producto registrarIngresoStock(Long id, BigDecimal cantidad, BigDecimal precioCosto);
    Producto registrarSalidaStock(Long id, BigDecimal cantidad, BigDecimal precioVenta);
}

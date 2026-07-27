package com.tuempresa.facturacion.infrastructure.adapters.input.rest;

import com.tuempresa.facturacion.domain.model.Producto;
import com.tuempresa.facturacion.domain.ports.in.AdministrarProductoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestAdapter {

    private final AdministrarProductoUseCase productoUseCase;

    public ProductoRestAdapter(AdministrarProductoUseCase productoUseCase) {
        this.productoUseCase = productoUseCase;
    }

    @PostMapping
    public ResponseEntity<Producto> registrar(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoUseCase.registrar(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return ResponseEntity.ok(productoUseCase.actualizar(id, producto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoUseCase.obtener(id));
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoUseCase.listar());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ingreso-stock")
    public ResponseEntity<Producto> ingresoStock(@PathVariable Long id,
                                                 @RequestParam BigDecimal cantidad,
                                                 @RequestParam BigDecimal costo) {
        return ResponseEntity.ok(productoUseCase.registrarIngresoStock(id, cantidad, costo));
    }

    @PostMapping("/{id}/salida-stock")
    public ResponseEntity<Producto> salidaStock(@PathVariable Long id,
                                                @RequestParam BigDecimal cantidad,
                                                @RequestParam BigDecimal venta) {
        return ResponseEntity.ok(productoUseCase.registrarSalidaStock(id, cantidad, venta));
    }
}

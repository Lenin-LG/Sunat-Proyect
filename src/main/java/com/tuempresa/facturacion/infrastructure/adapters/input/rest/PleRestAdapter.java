package com.tuempresa.facturacion.infrastructure.adapters.input.rest;

import com.tuempresa.facturacion.domain.model.PleFile;
import com.tuempresa.facturacion.domain.ports.in.GenerarPleUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/ple")
public class PleRestAdapter {

    private final GenerarPleUseCase useCase;

    public PleRestAdapter(GenerarPleUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/ventas")
    public ResponseEntity<byte[]> descargarVentas(@RequestParam int mes, @RequestParam int anio) {
        PleFile file = useCase.generarVentas(mes, anio);
        byte[] bytes = file.getContent().getBytes(StandardCharsets.ISO_8859_1);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(bytes);
    }

    @GetMapping("/compras")
    public ResponseEntity<byte[]> descargarCompras(@RequestParam int mes, @RequestParam int anio) {
        PleFile file = useCase.generarCompras(mes, anio);
        byte[] bytes = file.getContent().getBytes(StandardCharsets.ISO_8859_1);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(bytes);
    }
}

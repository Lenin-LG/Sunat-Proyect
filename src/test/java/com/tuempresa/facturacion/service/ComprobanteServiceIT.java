package com.tuempresa.facturacion.service;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.ports.in.EmitirComprobanteUseCase;
import com.tuempresa.facturacion.domain.ports.in.dto.ComprobanteCommand;
import com.tuempresa.facturacion.domain.ports.in.dto.ItemCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
class ComprobanteServiceIT {

    @Autowired
    private EmitirComprobanteUseCase emitirComprobanteUseCase;

    @Test
    void emiteBoletaEnBetaYEsAceptada() {
        ComprobanteCommand command = new ComprobanteCommand();
        command.setTipoDocumento("03");
        command.setSerie("B001");
        command.setClienteTipoDocumento("1");
        command.setClienteNumeroDocumento("12345678");
        command.setClienteNombre("CLIENTE DE PRUEBA");

        ItemCommand item = new ItemCommand();
        item.setDescripcion("PRODUCTO DE PRUEBA");
        item.setCantidad(BigDecimal.ONE);
        item.setPrecioUnitario(new BigDecimal("100.00"));
        command.setItems(List.of(item));

        Comprobante resultado = emitirComprobanteUseCase.emitir(command);

        assertThat(resultado.getEstado()).isEqualTo(Comprobante.EstadoComprobante.ACEPTADO);
        assertThat(resultado.getSunatResponseCode()).isEqualTo("0");
    }
}

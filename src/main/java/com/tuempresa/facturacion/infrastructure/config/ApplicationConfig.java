package com.tuempresa.facturacion.infrastructure.config;

import com.tuempresa.facturacion.domain.ports.in.EmitirComprobanteUseCase;
import com.tuempresa.facturacion.application.service.ComprobanteService;
import com.tuempresa.facturacion.domain.ports.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Configuration
public class ApplicationConfig {

    @Bean
    public EmitirComprobanteUseCase emitirComprobanteUseCase(
            ComprobantePersistencePort comprobantePersistencePort,
            EmpresaPersistencePort empresaPersistencePort,
            XmlBuilderPort xmlBuilderPort,
            FirmaDigitalPort firmaDigitalPort,
            SunatSoapPort sunatSoapPort,
            SunatProperties sunatProperties,
            PrivateKey privateKey,
            X509Certificate certificado) {
        return new ComprobanteService(
                comprobantePersistencePort,
                empresaPersistencePort,
                xmlBuilderPort,
                firmaDigitalPort,
                sunatSoapPort,
                sunatProperties.getRuc().toString(),
                privateKey,
                certificado);
    }
}

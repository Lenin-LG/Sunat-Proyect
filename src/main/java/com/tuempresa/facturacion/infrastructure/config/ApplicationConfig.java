package com.tuempresa.facturacion.infrastructure.config;

import com.tuempresa.facturacion.domain.ports.in.EmitirComprobanteUseCase;
import com.tuempresa.facturacion.domain.ports.in.AdministrarProductoUseCase;
import com.tuempresa.facturacion.domain.ports.in.AdministrarClienteUseCase;
import com.tuempresa.facturacion.domain.ports.in.AnularComprobanteUseCase;
import com.tuempresa.facturacion.domain.ports.in.EmitirGuiaRemisionUseCase;
import com.tuempresa.facturacion.domain.ports.in.AdministrarEmpresaUseCase;
import com.tuempresa.facturacion.application.service.ComprobanteService;
import com.tuempresa.facturacion.application.service.ProductoService;
import com.tuempresa.facturacion.application.service.ClienteService;
import com.tuempresa.facturacion.application.service.AnulacionService;
import com.tuempresa.facturacion.application.service.GuiaRemisionService;
import com.tuempresa.facturacion.application.service.EmpresaService;
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
            ProductoPersistencePort productoPersistencePort,
            KardexPersistencePort kardexPersistencePort,
            SunatProperties sunatProperties,
            PrivateKey privateKey,
            X509Certificate certificado) {
        return new ComprobanteService(
                comprobantePersistencePort,
                empresaPersistencePort,
                xmlBuilderPort,
                firmaDigitalPort,
                sunatSoapPort,
                productoPersistencePort,
                kardexPersistencePort,
                sunatProperties.getRuc().toString(),
                privateKey,
                certificado);
    }

    @Bean
    public AdministrarProductoUseCase administrarProductoUseCase(
            ProductoPersistencePort productoPersistencePort,
            KardexPersistencePort kardexPersistencePort) {
        return new ProductoService(productoPersistencePort, kardexPersistencePort);
    }

    @Bean
    public AdministrarClienteUseCase administrarClienteUseCase(
            EntidadPersistencePort entidadPersistencePort,
            ConsultaDocumentoPort consultaDocumentoPort) {
        return new ClienteService(entidadPersistencePort, consultaDocumentoPort);
    }

    @Bean
    public AnularComprobanteUseCase anularComprobanteUseCase(
            ComprobantePersistencePort comprobantePersistencePort,
            EmpresaPersistencePort empresaPersistencePort,
            XmlBuilderPort xmlBuilderPort,
            FirmaDigitalPort firmaDigitalPort,
            SunatSoapPort sunatSoapPort,
            SunatProperties sunatProperties,
            PrivateKey privateKey,
            X509Certificate certificado) {
        return new AnulacionService(
                comprobantePersistencePort,
                empresaPersistencePort,
                xmlBuilderPort,
                firmaDigitalPort,
                sunatSoapPort,
                sunatProperties.getRuc().toString(),
                privateKey,
                certificado);
    }

    @Bean
    public EmitirGuiaRemisionUseCase emitirGuiaRemisionUseCase(
            GuiaRemisionPersistencePort guiaPersistencePort,
            EmpresaPersistencePort empresaPersistencePort,
            EntidadPersistencePort entidadPersistencePort,
            ChoferPersistencePort choferPersistencePort,
            VehiculoPersistencePort vehiculoPersistencePort,
            GuiaXmlBuilderPort xmlBuilderPort,
            FirmaDigitalPort firmaDigitalPort,
            SunatGuiaSoapPort sunatGuiaSoapPort,
            SunatProperties sunatProperties,
            PrivateKey privateKey,
            X509Certificate certificado,
            ComprobantePersistencePort comprobantePersistencePort) {
        return new GuiaRemisionService(
                guiaPersistencePort,
                empresaPersistencePort,
                entidadPersistencePort,
                choferPersistencePort,
                vehiculoPersistencePort,
                xmlBuilderPort,
                firmaDigitalPort,
                sunatGuiaSoapPort,
                sunatProperties.getRuc().toString(),
                privateKey,
                certificado,
                comprobantePersistencePort);
    }

    @Bean
    public AdministrarEmpresaUseCase administrarEmpresaUseCase(
            EmpresaPersistencePort empresaPersistencePort) {
        return new EmpresaService(empresaPersistencePort);
    }
}

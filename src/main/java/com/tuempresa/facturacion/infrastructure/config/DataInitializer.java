package com.tuempresa.facturacion.infrastructure.config;

import com.tuempresa.facturacion.domain.model.Empresa;
import com.tuempresa.facturacion.domain.ports.out.EmpresaPersistencePort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmpresaPersistencePort empresaPersistencePort;
    private final SunatProperties sunatProperties;

    public DataInitializer(EmpresaPersistencePort empresaPersistencePort, SunatProperties sunatProperties) {
        this.empresaPersistencePort = empresaPersistencePort;
        this.sunatProperties = sunatProperties;
    }

    @Override
    public void run(String... args) {
        if (empresaPersistencePort.findByRuc(sunatProperties.getRuc()) != null) {
            return;
        }
        Empresa empresa = new Empresa();
        empresa.setRuc(sunatProperties.getRuc());
        empresa.setRazonSocial("EMPRESA DE PRUEBA SAC");
        empresa.setNombreComercial("EMPRESA DE PRUEBA SAC");
        empresa.setUbigeo("150101");
        empresa.setDepartamento("LIMA");
        empresa.setProvincia("LIMA");
        empresa.setDistrito("LIMA");
        empresa.setDireccionFiscal("AV. PRUEBA 123");
        empresaPersistencePort.save(empresa);
    }
}

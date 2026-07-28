package com.tuempresa.facturacion.infrastructure.config;

import com.tuempresa.facturacion.domain.model.Empresa;
import com.tuempresa.facturacion.domain.ports.out.EmpresaPersistencePort;
import com.tuempresa.facturacion.domain.ports.out.ChoferPersistencePort;
import com.tuempresa.facturacion.domain.ports.out.VehiculoPersistencePort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmpresaPersistencePort empresaPersistencePort;
    private final SunatProperties sunatProperties;
    private final ChoferPersistencePort choferPersistencePort;
    private final VehiculoPersistencePort vehiculoPersistencePort;

    public DataInitializer(EmpresaPersistencePort empresaPersistencePort,
                           SunatProperties sunatProperties,
                           ChoferPersistencePort choferPersistencePort,
                           VehiculoPersistencePort vehiculoPersistencePort) {
        this.empresaPersistencePort = empresaPersistencePort;
        this.sunatProperties = sunatProperties;
        this.choferPersistencePort = choferPersistencePort;
        this.vehiculoPersistencePort = vehiculoPersistencePort;
    }

    @Override
    public void run(String... args) {
        if (empresaPersistencePort.findByRuc(sunatProperties.getRuc()) == null) {
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

        if (choferPersistencePort.findAll().isEmpty()) {
            choferPersistencePort.save(com.tuempresa.facturacion.domain.model.Chofer.builder()
                    .tipoDocumento("1")
                    .numeroDocumento("44444444")
                    .nombre("JUAN PEREZ TRANSPORTISTA")
                    .licenciaConducir("Q12345678")
                    .build());
        }

        if (vehiculoPersistencePort.findAll().isEmpty()) {
            vehiculoPersistencePort.save(com.tuempresa.facturacion.domain.model.Vehiculo.builder()
                    .placa("ABC-123")
                    .marca("TOYOTA")
                    .modelo("HILUX")
                    .nroAutorizacion("AUT-999")
                    .build());
        }
    }
}

package com.tuempresa.facturacion.application.service;

import com.tuempresa.facturacion.domain.model.*;
import com.tuempresa.facturacion.domain.ports.in.EmitirGuiaRemisionUseCase;
import com.tuempresa.facturacion.domain.ports.in.dto.GuiaCommand;
import com.tuempresa.facturacion.domain.ports.out.*;
import org.w3c.dom.Document;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.LocalDate;

public class GuiaRemisionService implements EmitirGuiaRemisionUseCase {

    private final GuiaRemisionPersistencePort guiaPersistencePort;
    private final EmpresaPersistencePort empresaPersistencePort;
    private final EntidadPersistencePort entidadPersistencePort;
    private final ChoferPersistencePort choferPersistencePort;
    private final VehiculoPersistencePort vehiculoPersistencePort;
    private final GuiaXmlBuilderPort xmlBuilderPort;
    private final FirmaDigitalPort firmaDigitalPort;
    private final SunatGuiaSoapPort sunatGuiaSoapPort;
    private final String rucEmisor;
    private final PrivateKey privateKey;
    private final X509Certificate certificado;
    private final ComprobantePersistencePort comprobantePersistencePort;

    public GuiaRemisionService(GuiaRemisionPersistencePort guiaPersistencePort,
                               EmpresaPersistencePort empresaPersistencePort,
                               EntidadPersistencePort entidadPersistencePort,
                               ChoferPersistencePort choferPersistencePort,
                               VehiculoPersistencePort vehiculoPersistencePort,
                               GuiaXmlBuilderPort xmlBuilderPort,
                               FirmaDigitalPort firmaDigitalPort,
                               SunatGuiaSoapPort sunatGuiaSoapPort,
                               String rucEmisor,
                               PrivateKey privateKey,
                               X509Certificate certificado,
                               ComprobantePersistencePort comprobantePersistencePort) {
        this.guiaPersistencePort = guiaPersistencePort;
        this.empresaPersistencePort = empresaPersistencePort;
        this.entidadPersistencePort = entidadPersistencePort;
        this.choferPersistencePort = choferPersistencePort;
        this.vehiculoPersistencePort = vehiculoPersistencePort;
        this.xmlBuilderPort = xmlBuilderPort;
        this.firmaDigitalPort = firmaDigitalPort;
        this.sunatGuiaSoapPort = sunatGuiaSoapPort;
        this.rucEmisor = rucEmisor;
        this.privateKey = privateKey;
        this.certificado = certificado;
        this.comprobantePersistencePort = comprobantePersistencePort;
    }

    @Override
    public GuiaRemision emitir(GuiaCommand command) {
        Empresa empresa = empresaPersistencePort.findByRuc(rucEmisor);
        if (empresa == null) {
            throw new IllegalStateException("Empresa no registrada con RUC emisor.");
        }
        Entidad cliente = entidadPersistencePort.findById(command.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Chofer chofer = choferPersistencePort.findById(command.getChoferId())
                .orElseThrow(() -> new IllegalArgumentException("Chofer no encontrado"));
        Vehiculo vehiculo = vehiculoPersistencePort.findById(command.getVehiculoId())
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        int sigNum = siguienteNumero(command.getTipoGuia(), command.getSerie());

        GuiaRemision de = GuiaRemision.builder()
                .tipoGuia(command.getTipoGuia())
                .serie(command.getSerie())
                .numero(sigNum)
                .fechaEmision(LocalDate.now())
                .comprobanteId(command.getComprobanteId())
                .clienteId(command.getClienteId())
                .choferId(command.getChoferId())
                .vehiculoId(command.getVehiculoId())
                .motivoTraslado(command.getMotivoTraslado())
                .pesoTotal(command.getPesoTotal())
                .build();

        de = guiaPersistencePort.save(de);

        java.util.List<ComprobanteDetalle> detalles = new java.util.ArrayList<>();
        if (command.getComprobanteId() != null) {
            comprobantePersistencePort.findById(command.getComprobanteId()).ifPresent(comp -> {
                detalles.addAll(comp.getDetalles());
            });
        }

        Document xml = xmlBuilderPort.construir(de, empresa, cliente, chofer, vehiculo, detalles);
        com.tuempresa.facturacion.infrastructure.config.DynamicCertLoader.CertKeys keys = 
                com.tuempresa.facturacion.infrastructure.config.DynamicCertLoader.load(empresa, privateKey, certificado);
        Document xmlFirmado = firmaDigitalPort.firmar(xml, keys.privateKey, keys.certificate);

        String nombreArchivo = "%s-%s-%s-%d".formatted(empresa.getRuc(), de.getTipoGuia(), de.getSerie(), de.getNumero());
        RespuestaSunat respuesta = sunatGuiaSoapPort.enviarGuia(nombreArchivo, xmlFirmado);

        de.setSunatResponseCode(truncate(respuesta.getResponseCode(), 255));
        de.setSunatDescription(truncate(respuesta.getDescription(), 1000));

        return guiaPersistencePort.save(de);
    }

    private int siguienteNumero(String tipoGuia, String serie) {
        return guiaPersistencePort.findTopByTipoGuiaAndSerieOrderByNumeroDesc(tipoGuia, serie)
                .map(g -> g.getNumero() + 1)
                .orElse(1);
    }

    private String truncate(String str, int length) {
        if (str == null) return null;
        return str.length() > length ? str.substring(0, length) : str;
    }
}

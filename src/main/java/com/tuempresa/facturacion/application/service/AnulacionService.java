package com.tuempresa.facturacion.application.service;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.model.Empresa;
import com.tuempresa.facturacion.domain.model.RespuestaSunat;
import com.tuempresa.facturacion.domain.ports.in.AnularComprobanteUseCase;
import com.tuempresa.facturacion.domain.ports.out.*;
import org.w3c.dom.Document;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AnulacionService implements AnularComprobanteUseCase {

    private final ComprobantePersistencePort comprobantePersistencePort;
    private final EmpresaPersistencePort empresaPersistencePort;
    private final XmlBuilderPort xmlBuilderPort;
    private final FirmaDigitalPort firmaDigitalPort;
    private final SunatSoapPort sunatSoapPort;
    private final String rucEmisor;
    private final PrivateKey privateKey;
    private final X509Certificate certificado;

    public AnulacionService(ComprobantePersistencePort comprobantePersistencePort,
                            EmpresaPersistencePort empresaPersistencePort,
                            XmlBuilderPort xmlBuilderPort,
                            FirmaDigitalPort firmaDigitalPort,
                            SunatSoapPort sunatSoapPort,
                            String rucEmisor,
                            PrivateKey privateKey,
                            X509Certificate certificado) {
        this.comprobantePersistencePort = comprobantePersistencePort;
        this.empresaPersistencePort = empresaPersistencePort;
        this.xmlBuilderPort = xmlBuilderPort;
        this.firmaDigitalPort = firmaDigitalPort;
        this.sunatSoapPort = sunatSoapPort;
        this.rucEmisor = rucEmisor;
        this.privateKey = privateKey;
        this.certificado = certificado;
    }

    @Override
    public Comprobante anular(Long id, String motivo) {
        Comprobante c = comprobantePersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el comprobante con id " + id));

        if (c.getEstado() != Comprobante.EstadoComprobante.ACEPTADO) {
            throw new IllegalStateException("Solo se pueden anular comprobantes aceptados por SUNAT");
        }

        if ("03".equals(c.getTipoDocumento())) {
            throw new IllegalArgumentException("Las boletas de venta no pueden ser anuladas mediante Comunicacion de Baja. Debe emitir una Nota de Credito para su anulacion.");
        }

        Empresa empresa = empresaPersistencePort.findByRuc(rucEmisor);

        // ID de baja: RA-YYYYMMDD-Correlativo
        String fechaHoyStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String correlativo = String.format("%03d", (int) (Math.random() * 1000));
        String idBaja = "RA-" + fechaHoyStr + "-" + correlativo;

        Document xml = xmlBuilderPort.construirBaja(c, empresa, motivo, idBaja);
        com.tuempresa.facturacion.infrastructure.config.DynamicCertLoader.CertKeys keys = 
                com.tuempresa.facturacion.infrastructure.config.DynamicCertLoader.load(empresa, privateKey, certificado);
        Document xmlFirmado = firmaDigitalPort.firmar(xml, keys.privateKey, keys.certificate);

        String nombreArchivo = empresa.getRuc() + "-" + idBaja;
        RespuestaSunat respuesta = sunatSoapPort.enviarResumen(nombreArchivo, xmlFirmado);

        if (respuesta.isAceptado()) {
            c.setSunatResponseCode(truncate(respuesta.getResponseCode(), 255));
            c.setSunatDescription(truncate("PENDIENTE TICKET: " + respuesta.getResponseCode(), 1000));
            c.setEstado(Comprobante.EstadoComprobante.PENDIENTE);
        } else {
            c.setSunatResponseCode(truncate(respuesta.getResponseCode(), 255));
            c.setSunatDescription(truncate(respuesta.getDescription(), 1000));
            c.setEstado(Comprobante.EstadoComprobante.ERROR);
        }

        return comprobantePersistencePort.save(c);
    }

    @Override
    public Comprobante consultarEstadoTicket(Long id) {
        Comprobante c = comprobantePersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el comprobante con id " + id));

        if (c.getEstado() != Comprobante.EstadoComprobante.PENDIENTE || c.getSunatResponseCode() == null) {
            throw new IllegalStateException("El comprobante no tiene un ticket de baja pendiente");
        }

        RespuestaSunat respuesta = sunatSoapPort.consultarTicket(c.getSunatResponseCode());

        if (respuesta.isAceptado()) {
            c.setEstado(Comprobante.EstadoComprobante.RECHAZADO); // Estado anulado localmente
            c.setSunatDescription(truncate("ANULADO: " + respuesta.getDescription(), 1000));
        } else if ("98".equals(respuesta.getResponseCode())) {
            c.setSunatDescription(truncate("PENDIENTE TICKET (EN PROCESO): " + c.getSunatResponseCode(), 1000));
        } else {
            c.setSunatDescription(truncate("ERROR CONSULTA TICKET: " + respuesta.getDescription(), 1000));
        }

        return comprobantePersistencePort.save(c);
    }

    private String truncate(String str, int length) {
        if (str == null) return null;
        return str.length() > length ? str.substring(0, length) : str;
    }
}

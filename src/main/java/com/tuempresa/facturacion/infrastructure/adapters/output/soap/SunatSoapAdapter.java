package com.tuempresa.facturacion.infrastructure.adapters.output.soap;

import com.tuempresa.facturacion.domain.model.RespuestaSunat;
import com.tuempresa.facturacion.domain.ports.out.SunatSoapPort;
import com.tuempresa.facturacion.infrastructure.config.SunatProperties;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class SunatSoapAdapter implements SunatSoapPort {

    private final SunatProperties properties;
    private final com.tuempresa.facturacion.domain.ports.out.EmpresaPersistencePort empresaPersistencePort;
    private final RestTemplate restTemplate = new RestTemplate();

    public SunatSoapAdapter(SunatProperties properties, com.tuempresa.facturacion.domain.ports.out.EmpresaPersistencePort empresaPersistencePort) {
        this.properties = properties;
        this.empresaPersistencePort = empresaPersistencePort;
    }

    private static class SunatConnectionConfig {
        String endpoint;
        String usernameToken;
        String passwordSol;
    }

    private SunatConnectionConfig obtenerConfiguracion(String ruc) {
        SunatConnectionConfig config = new SunatConnectionConfig();
        com.tuempresa.facturacion.domain.model.Empresa empresa = null;
        if (ruc != null && !ruc.isBlank()) {
            try {
                empresa = empresaPersistencePort.findByRuc(ruc);
            } catch (Exception e) {
                // ignore
            }
        }
        if (empresa != null) {
            config.endpoint = empresa.isModoProduccion() ? properties.getEndpointProduccion() : properties.getEndpointBeta();
            String user = empresa.isModoProduccion() ? empresa.getUsuarioSolProduccion() : properties.getUsuarioSol();
            config.usernameToken = ruc + (user != null ? user : "");
            config.passwordSol = empresa.isModoProduccion() ? empresa.getPasswordSolProduccion() : properties.getPasswordSol();
        } else {
            config.endpoint = properties.getEndpointActivo();
            config.usernameToken = properties.getUsernameToken();
            config.passwordSol = properties.getPasswordSol();
        }
        return config;
    }

    private String extraerRucDesdeXml(Document doc) {
        try {
            String ruc = xpathValue(doc, "//*[local-name()='AccountingSupplierParty']//*[local-name()='PartyIdentification']/*[local-name()='ID']");
            if (ruc == null || ruc.isBlank()) {
                ruc = xpathValue(doc, "//*[local-name()='AccountingSupplierParty']/*[local-name()='CustomerAssignedAccountID']");
            }
            return ruc;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public RespuestaSunat enviarComprobante(String nombreArchivoSinExtension, Document xmlFirmado) {
        try {
            byte[] zipBytes = comprimir(nombreArchivoSinExtension + ".xml", xmlFirmado);
            String contentFileBase64 = Base64.getEncoder().encodeToString(zipBytes);

            String ruc = nombreArchivoSinExtension.substring(0, 11);
            SunatConnectionConfig config = obtenerConfiguracion(ruc);

            String soapEnvelope = construirSobreSoap(nombreArchivoSinExtension + ".zip", contentFileBase64, config);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.set("SOAPAction", "urn:sendBill");

            HttpEntity<String> request = new HttpEntity<>(soapEnvelope, headers);
            String respuestaXml = restTemplate.postForObject(
                    config.endpoint, request, String.class);

            return parsearRespuesta(respuestaXml);

        } catch (Exception e) {
            return RespuestaSunat.rechazadoPorError("Error de comunicacion con SUNAT: " + e.getMessage());
        }
    }

    @Override
    public RespuestaSunat enviarResumen(String nombreArchivoSinExtension, Document xmlFirmado) {
        try {
            byte[] zipBytes = comprimir(nombreArchivoSinExtension + ".xml", xmlFirmado);
            String contentFileBase64 = Base64.getEncoder().encodeToString(zipBytes);

            String ruc = extraerRucDesdeXml(xmlFirmado);
            SunatConnectionConfig config = obtenerConfiguracion(ruc);

            String soapEnvelope = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.sunat.gob.pe">
                       <soapenv:Header>
                          <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
                             <wsse:UsernameToken>
                                <wsse:Username>%s</wsse:Username>
                                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">%s</wsse:Password>
                             </wsse:UsernameToken>
                          </wsse:Security>
                       </soapenv:Header>
                       <soapenv:Body>
                          <ser:sendSummary>
                             <fileName>%s</fileName>
                             <contentFile>%s</contentFile>
                          </ser:sendSummary>
                       </soapenv:Body>
                    </soapenv:Envelope>
                    """.formatted(config.usernameToken, config.passwordSol,
                            nombreArchivoSinExtension + ".zip", contentFileBase64);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.set("SOAPAction", "urn:sendSummary");

            HttpEntity<String> request = new HttpEntity<>(soapEnvelope, headers);
            String respuestaXml = restTemplate.postForObject(
                    config.endpoint, request, String.class);

            return parsearRespuestaResumen(respuestaXml);

        } catch (Exception e) {
            return RespuestaSunat.rechazadoPorError("Error al enviar resumen a SUNAT: " + e.getMessage());
        }
    }

    @Override
    public RespuestaSunat consultarTicket(String ticket) {
        try {
            SunatConnectionConfig config = obtenerConfiguracion(null);
            String soapEnvelope = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.sunat.gob.pe">
                       <soapenv:Header>
                          <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
                             <wsse:UsernameToken>
                                <wsse:Username>%s</wsse:Username>
                                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">%s</wsse:Password>
                             </wsse:UsernameToken>
                          </wsse:Security>
                       </soapenv:Header>
                       <soapenv:Body>
                          <ser:getStatus>
                             <ticket>%s</ticket>
                          </ser:getStatus>
                       </soapenv:Body>
                    </soapenv:Envelope>
                    """.formatted(config.usernameToken, config.passwordSol, ticket);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.set("SOAPAction", "urn:getStatus");

            HttpEntity<String> request = new HttpEntity<>(soapEnvelope, headers);
            String respuestaXml = restTemplate.postForObject(
                    config.endpoint, request, String.class);

            return parsearRespuestaTicket(respuestaXml);

        } catch (Exception e) {
            return RespuestaSunat.rechazadoPorError("Error al consultar ticket en SUNAT: " + e.getMessage());
        }
    }

    @Override
    public RespuestaSunat consultarCdr(String ruc, String tipoCpe, String serie, int numero) {
        try {
            SunatConnectionConfig config = obtenerConfiguracion(ruc);
            String soapEnvelope = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.sunat.gob.pe">
                       <soapenv:Header>
                          <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
                             <wsse:UsernameToken>
                                <wsse:Username>%s</wsse:Username>
                                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">%s</wsse:Password>
                             </wsse:UsernameToken>
                          </wsse:Security>
                       </soapenv:Header>
                       <soapenv:Body>
                          <ser:getStatusCdr>
                             <ruc>%s</ruc>
                             <tipoComprobante>%s</tipoComprobante>
                             <serie>%s</serie>
                             <numero>%d</numero>
                          </ser:getStatusCdr>
                       </soapenv:Body>
                    </soapenv:Envelope>
                    """.formatted(config.usernameToken, config.passwordSol,
                            ruc, tipoCpe, serie, numero);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.set("SOAPAction", "urn:getStatusCdr");

            String endpoint = config.endpoint;
            if (endpoint.contains("billService")) {
                endpoint = endpoint.replace("billService", "billConsult");
            }

            HttpEntity<String> request = new HttpEntity<>(soapEnvelope, headers);
            String respuestaXml = restTemplate.postForObject(endpoint, request, String.class);

            return parsearRespuestaStatusCdr(respuestaXml);

        } catch (Exception e) {
            return RespuestaSunat.rechazadoPorError("Error al consultar CDR en SUNAT: " + e.getMessage());
        }
    }

    private byte[] comprimir(String nombreXml, Document doc) throws Exception {
        ByteArrayOutputStream xmlBytes = new ByteArrayOutputStream();
        javax.xml.transform.TransformerFactory.newInstance().newTransformer()
                .transform(new javax.xml.transform.dom.DOMSource(doc),
                        new javax.xml.transform.stream.StreamResult(xmlBytes));

        ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(zipBytes)) {
            zos.putNextEntry(new ZipEntry(nombreXml));
            zos.write(xmlBytes.toByteArray());
            zos.closeEntry();
        }
        return zipBytes.toByteArray();
    }

    private String construirSobreSoap(String fileName, String contentFileBase64, SunatConnectionConfig config) {
        return """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.sunat.gob.pe">
                   <soapenv:Header>
                      <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
                         <wsse:UsernameToken>
                            <wsse:Username>%s</wsse:Username>
                            <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">%s</wsse:Password>
                         </wsse:UsernameToken>
                      </wsse:Security>
                   </soapenv:Header>
                   <soapenv:Body>
                      <ser:sendBill>
                         <fileName>%s</fileName>
                         <contentFile>%s</contentFile>
                      </ser:sendBill>
                   </soapenv:Body>
                </soapenv:Envelope>
                """
                .formatted(config.usernameToken, config.passwordSol,
                        fileName, contentFileBase64);
    }

    private RespuestaSunat parsearRespuesta(String respuestaXml) throws Exception {
        Document soapDoc = parseXml(respuestaXml);

        NodeList faults = soapDoc.getElementsByTagNameNS("*", "Fault");
        if (faults.getLength() > 0) {
            String faultString = xpathValue(soapDoc, "//*[local-name()='faultstring']");
            return RespuestaSunat.rechazadoPorError(faultString);
        }

        String applicationResponseB64 = xpathValue(soapDoc, "//*[local-name()='applicationResponse']");
        byte[] cdrZip = Base64.getDecoder().decode(applicationResponseB64.trim());
        String cdrXml = extraerXmlDelZip(cdrZip);

        Document cdrDoc = parseXml(cdrXml);
        String responseCode = xpathValue(cdrDoc, "//*[local-name()='ResponseCode']");
        String description = xpathValue(cdrDoc, "//*[local-name()='Description']");
        List<String> notes = xpathValues(cdrDoc, "//*[local-name()='Note']");

        boolean aceptado = "0".equals(responseCode);
        return new RespuestaSunat(aceptado, responseCode, description, notes);
    }

    private RespuestaSunat parsearRespuestaResumen(String respuestaXml) throws Exception {
        Document soapDoc = parseXml(respuestaXml);

        NodeList faults = soapDoc.getElementsByTagNameNS("*", "Fault");
        if (faults.getLength() > 0) {
            String faultString = xpathValue(soapDoc, "//*[local-name()='faultstring']");
            return RespuestaSunat.rechazadoPorError(faultString);
        }

        String ticket = xpathValue(soapDoc, "//*[local-name()='ticket']");
        if (ticket == null || ticket.isBlank()) {
            return RespuestaSunat.rechazadoPorError("No se recibio ticket de SUNAT");
        }

        return new RespuestaSunat(true, ticket, "TICKET RECIBIDO", new ArrayList<>());
    }

    private RespuestaSunat parsearRespuestaTicket(String respuestaXml) throws Exception {
        Document soapDoc = parseXml(respuestaXml);

        NodeList faults = soapDoc.getElementsByTagNameNS("*", "Fault");
        if (faults.getLength() > 0) {
            String faultString = xpathValue(soapDoc, "//*[local-name()='faultstring']");
            return RespuestaSunat.rechazadoPorError(faultString);
        }

        String statusCode = xpathValue(soapDoc, "//*[local-name()='statusCode']");
        if ("98".equals(statusCode)) {
            return new RespuestaSunat(false, "98", "EL TICKET ESTA EN PROCESO", new ArrayList<>());
        }

        String contentB64 = xpathValue(soapDoc, "//*[local-name()='content']");
        if (contentB64 == null || contentB64.isBlank()) {
            return RespuestaSunat.rechazadoPorError("No se recibio CDR en la respuesta del ticket");
        }

        byte[] cdrZip = Base64.getDecoder().decode(contentB64.trim());
        String cdrXml = extraerXmlDelZip(cdrZip);

        Document cdrDoc = parseXml(cdrXml);
        String responseCode = xpathValue(cdrDoc, "//*[local-name()='ResponseCode']");
        String description = xpathValue(cdrDoc, "//*[local-name()='Description']");
        List<String> notes = xpathValues(cdrDoc, "//*[local-name()='Note']");

        boolean aceptado = "0".equals(responseCode);
        return new RespuestaSunat(aceptado, responseCode, description, notes);
    }

    private RespuestaSunat parsearRespuestaStatusCdr(String respuestaXml) throws Exception {
        Document soapDoc = parseXml(respuestaXml);

        NodeList faults = soapDoc.getElementsByTagNameNS("*", "Fault");
        if (faults.getLength() > 0) {
            String faultString = xpathValue(soapDoc, "//*[local-name()='faultstring']");
            return RespuestaSunat.rechazadoPorError(faultString);
        }

        String statusCode = xpathValue(soapDoc, "//*[local-name()='statusCode']");
        String statusMessage = xpathValue(soapDoc, "//*[local-name()='statusMessage']");

        if ("0001".equals(statusCode)) {
            // El comprobante existe y está aceptado
            return new RespuestaSunat(true, "0", statusMessage, new ArrayList<>());
        }

        return new RespuestaSunat(false, statusCode, statusMessage, new ArrayList<>());
    }

    private String extraerXmlDelZip(byte[] zipBytes) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xml")) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    zis.transferTo(out);
                    return out.toString("UTF-8");
                }
            }
        }
        throw new IllegalStateException("El CDR recibido no contiene un archivo XML");
    }

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    private String xpathValue(Document doc, String expression) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return (String) xpath.evaluate(expression, doc, XPathConstants.STRING);
    }

    private List<String> xpathValues(Document doc, String expression) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            result.add(nodes.item(i).getTextContent());
        }
        return result;
    }
}

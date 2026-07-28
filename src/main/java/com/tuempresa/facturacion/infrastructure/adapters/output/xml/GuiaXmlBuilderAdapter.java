package com.tuempresa.facturacion.infrastructure.adapters.output.xml;

import com.tuempresa.facturacion.domain.model.*;
import com.tuempresa.facturacion.domain.ports.out.GuiaXmlBuilderPort;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.math.RoundingMode;

@Service
public class GuiaXmlBuilderAdapter implements GuiaXmlBuilderPort {

    private static final String NS_DESPATCH = "urn:oasis:names:specification:ubl:schema:xsd:DespatchAdvice-2";
    private static final String NS_CAC = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
    private static final String NS_CBC = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private static final String NS_EXT = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
    private static final String NS_DS = "http://www.w3.org/2000/09/xmldsig#";

    @Override
    public Document construir(GuiaRemision de, Empresa empresa, Entidad cliente, Chofer chofer, Vehiculo vehiculo,
            java.util.List<ComprobanteDetalle> detalles) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().newDocument();

            Element root = doc.createElementNS(NS_DESPATCH, "DespatchAdvice");
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", NS_DESPATCH);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cac", NS_CAC);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cbc", NS_CBC);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ext", NS_EXT);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ds", NS_DS);
            doc.appendChild(root);

            root.appendChild(buildUblExtensions(doc));
            root.appendChild(cbc(doc, "UBLVersionID", "2.1"));
            root.appendChild(cbc(doc, "CustomizationID", "2.0"));
            root.appendChild(cbc(doc, "ID", de.getSerie() + "-" + de.getNumero()));
            root.appendChild(cbc(doc, "IssueDate", de.getFechaEmision().toString()));
            root.appendChild(cbc(doc, "IssueTime", "00:00:00"));

            Element typeCode = cbc(doc, "DespatchAdviceTypeCode", de.getTipoGuia());
            typeCode.setAttribute("listAgencyName", "PE:SUNAT");
            typeCode.setAttribute("listName", "Tipo de Documento");
            typeCode.setAttribute("listURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo01");
            root.appendChild(typeCode);

            root.appendChild(buildFirmaNegocio(doc, empresa));
            root.appendChild(buildSupplierParty(doc, empresa));
            root.appendChild(buildCustomerParty(doc, cliente));

            // Shipments
            Element shipment = cac(doc, "Shipment");
            shipment.appendChild(cbc(doc, "HandlingCode", de.getMotivoTraslado()));
            shipment.appendChild(cbc(doc, "Information", "TRASLADO POR MOTIVO " + de.getMotivoTraslado()));

            Element grossWeight = cbc(doc, "GrossWeightMeasure",
                    de.getPesoTotal().setScale(2, RoundingMode.HALF_UP).toPlainString());
            grossWeight.setAttribute("unitCode", "KGM");
            shipment.appendChild(grossWeight);

            // Transport Stage
            Element stage = cac(doc, "ShipmentStage");
            Element transit = cac(doc, "TransitPeriod");
            transit.appendChild(cbc(doc, "StartDate", de.getFechaEmision().toString()));
            stage.appendChild(transit);

            // Driver Person
            Element driverEl = cac(doc, "DriverPerson");
            driverEl.appendChild(cbc(doc, "FirstName", chofer.getNombre()));
            Element identity = cac(doc, "IdentityDocumentReference");
            Element cardId = cbc(doc, "ID", chofer.getNumeroDocumento());
            cardId.setAttribute("schemeID", chofer.getTipoDocumento());
            identity.appendChild(cardId);
            driverEl.appendChild(identity);

            // License
            Element licenseEl = cac(doc, "ActiveLicense");
            licenseEl.appendChild(cbc(doc, "ID", chofer.getLicenciaConducir()));
            driverEl.appendChild(licenseEl);
            stage.appendChild(driverEl);
            shipment.appendChild(stage);

            // Delivery Points
            Element delivery = cac(doc, "Delivery");
            Element delAddress = cac(doc, "DeliveryAddress");
            delAddress.appendChild(cbc(doc, "ID", "150101")); // Ubigeo test
            Element delLine = cac(doc, "AddressLine");
            delLine.appendChild(cbc(doc, "Line",
                    cliente.getDireccion() != null ? cliente.getDireccion() : "DIRECCION LLEGADA MOCK"));
            delAddress.appendChild(delLine);
            delivery.appendChild(delAddress);

            // Despatch Points (Departure)
            Element despatch = cac(doc, "Despatch");
            Element depAddress = cac(doc, "DespatchAddress");
            depAddress.appendChild(cbc(doc, "ID", empresa.getUbigeo()));
            Element depLine = cac(doc, "AddressLine");
            depLine.appendChild(cbc(doc, "Line", empresa.getDireccionFiscal()));
            depAddress.appendChild(depLine);
            despatch.appendChild(depAddress);
            delivery.appendChild(despatch);

            shipment.appendChild(delivery);

            // Vehicles / Transport Equipment
            Element trUnit = cac(doc, "TransportHandlingUnit");
            Element trEquip = cac(doc, "TransportEquipment");
            trEquip.appendChild(cbc(doc, "ID", vehiculo.getPlaca()));
            trUnit.appendChild(trEquip);
            shipment.appendChild(trUnit);

            root.appendChild(shipment);

            int lineId = 1;
            if (detalles != null) {
                for (ComprobanteDetalle detail : detalles) {
                    root.appendChild(buildDespatchLine(doc, detail, lineId++));
                }
            }

            return doc;
        } catch (Exception e) {
            throw new IllegalStateException("Error construyendo XML UBL de Guia de Remision", e);
        }
    }

    private Element buildDespatchLine(Document doc, ComprobanteDetalle detail, int lineId) {
        Element line = cac(doc, "DespatchLine");
        line.appendChild(cbc(doc, "ID", String.valueOf(lineId)));

        Element qty = cbc(doc, "DeliveredQuantity",
                detail.getCantidad().setScale(2, RoundingMode.HALF_UP).toPlainString());
        qty.setAttribute("unitCode", detail.getTipoUnidad() != null ? detail.getTipoUnidad() : "NIU");
        line.appendChild(qty);

        Element itemEl = cac(doc, "Item");
        itemEl.appendChild(cbc(doc, "Description", detail.getDescripcion()));
        if (detail.getCodigoInterno() != null && !detail.getCodigoInterno().isBlank()) {
            Element sellersId = cac(doc, "SellersItemIdentification");
            sellersId.appendChild(cbc(doc, "ID", detail.getCodigoInterno()));
            itemEl.appendChild(sellersId);
        }
        line.appendChild(itemEl);

        return line;
    }

    private Element buildUblExtensions(Document doc) {
        Element extensions = doc.createElementNS(NS_EXT, "ext:UBLExtensions");
        Element extension = doc.createElementNS(NS_EXT, "ext:UBLExtension");
        Element content = doc.createElementNS(NS_EXT, "ext:ExtensionContent");

        extension.appendChild(content);
        extensions.appendChild(extension);
        return extensions;
    }

    private Element buildFirmaNegocio(Document doc, Empresa empresa) {
        Element signature = cac(doc, "Signature");
        signature.appendChild(cbc(doc, "ID", empresa.getRuc()));

        Element signatoryParty = cac(doc, "SignatoryParty");
        Element partyId = cac(doc, "PartyIdentification");
        partyId.appendChild(cbc(doc, "ID", empresa.getRuc()));
        Element partyName = cac(doc, "PartyName");
        partyName.appendChild(cbc(doc, "Name", empresa.getRazonSocial()));
        signatoryParty.appendChild(partyId);
        signatoryParty.appendChild(partyName);

        Element digitalSigAttachment = cac(doc, "DigitalSignatureAttachment");
        Element externalRef = cac(doc, "ExternalReference");
        externalRef.appendChild(cbc(doc, "URI", empresa.getRuc()));
        digitalSigAttachment.appendChild(externalRef);

        signature.appendChild(signatoryParty);
        signature.appendChild(digitalSigAttachment);
        return signature;
    }

    private Element buildSupplierParty(Document doc, Empresa empresa) {
        Element supplierParty = cac(doc, "DespatchSupplierParty");
        Element party = cac(doc, "Party");

        Element partyId = cac(doc, "PartyIdentification");
        Element id = cbc(doc, "ID", empresa.getRuc());
        id.setAttribute("schemeID", "6");
        partyId.appendChild(id);

        Element legalEntity = cac(doc, "PartyLegalEntity");
        legalEntity.appendChild(cbc(doc, "RegistrationName", empresa.getRazonSocial()));

        party.appendChild(partyId);
        party.appendChild(legalEntity);
        supplierParty.appendChild(party);
        return supplierParty;
    }

    private Element buildCustomerParty(Document doc, Entidad cliente) {
        Element customerParty = cac(doc, "DeliveryCustomerParty");
        Element party = cac(doc, "Party");

        Element partyId = cac(doc, "PartyIdentification");
        Element id = cbc(doc, "ID", cliente.getNumeroDocumento());
        id.setAttribute("schemeID", cliente.getTipoEntidadId());
        partyId.appendChild(id);

        Element legalEntity = cac(doc, "PartyLegalEntity");
        legalEntity.appendChild(cbc(doc, "RegistrationName", cliente.getNombreRazonSocial()));

        party.appendChild(partyId);
        party.appendChild(legalEntity);
        customerParty.appendChild(party);
        return customerParty;
    }

    private Element cac(Document doc, String localName) {
        return doc.createElementNS(NS_CAC, "cac:" + localName);
    }

    private Element cbc(Document doc, String localName, String value) {
        Element el = doc.createElementNS(NS_CBC, "cbc:" + localName);
        el.setTextContent(value);
        return el;
    }
}

package com.tuempresa.facturacion.infrastructure.adapters.output.xml;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.model.ComprobanteDetalle;
import com.tuempresa.facturacion.domain.model.Empresa;
import com.tuempresa.facturacion.domain.ports.out.XmlBuilderPort;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class XmlBuilderAdapter implements XmlBuilderPort {

    private static final String NS_INVOICE = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2";
    private static final String NS_CAC = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
    private static final String NS_CBC = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private static final String NS_EXT = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
    private static final String NS_DS = "http://www.w3.org/2000/09/xmldsig#";

    private static final BigDecimal IGV_PORCENTAJE = new BigDecimal("0.18");

    @Override
    public Document construir(Comprobante c, Empresa empresa) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().newDocument();

            Element invoice = ns(doc, NS_INVOICE, "Invoice");

            invoice.setAttributeNS(
                "http://www.w3.org/2000/xmlns/",
                "xmlns",
                NS_INVOICE
            );

            invoice.setAttributeNS(
                "http://www.w3.org/2000/xmlns/",
                "xmlns:cac",
                NS_CAC
            );

            invoice.setAttributeNS(
                "http://www.w3.org/2000/xmlns/",
                "xmlns:cbc",
                NS_CBC
            );

            invoice.setAttributeNS(
                "http://www.w3.org/2000/xmlns/",
                "xmlns:ext",
                NS_EXT
            );

            invoice.setAttributeNS(
                "http://www.w3.org/2000/xmlns/",
                "xmlns:ds",
                NS_DS
            );

            doc.appendChild(invoice);

            invoice.appendChild(buildUblExtensions(doc));
            invoice.appendChild(cbc(doc, "UBLVersionID", "2.1"));
            invoice.appendChild(cbc(doc, "CustomizationID", "2.0"));
            invoice.appendChild(cbc(doc, "ID", c.getSerie() + "-" + c.getNumero()));
            invoice.appendChild(cbc(doc, "IssueDate", c.getFechaEmision().toString()));
            invoice.appendChild(cbc(doc, "IssueTime", "00:00:00"));

            Element tipoDoc = cbc(doc, "InvoiceTypeCode", c.getTipoDocumento());
            tipoDoc.setAttribute("listID", "0101");
            tipoDoc.setAttribute("listAgencyName", "PE:SUNAT");
            tipoDoc.setAttribute("listName", "Tipo de Documento");
            tipoDoc.setAttribute("listURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo01");
            invoice.appendChild(tipoDoc);

            Element note = cbc(doc, "Note", "IMPORTE EN LETRAS PENDIENTE DE CONVERSION");
            note.setAttribute("languageLocaleID", "1000");
            invoice.appendChild(note);

            Element moneda = cbc(doc, "DocumentCurrencyCode", "PEN");
            moneda.setAttribute("listID", "ISO 4217 Alpha");
            moneda.setAttribute("listName", "Currency");
            moneda.setAttribute("listAgencyName", "United Nations Economic Commission for Europe");
            invoice.appendChild(moneda);

            invoice.appendChild(buildFirmaNegocio(doc, empresa));
            invoice.appendChild(buildSupplierParty(doc, empresa));
            invoice.appendChild(buildCustomerParty(doc, c));

            if ("01".equals(c.getTipoDocumento()) || "03".equals(c.getTipoDocumento())) {
                Element paymentTerms = cac(doc, "PaymentTerms");
                paymentTerms.appendChild(cbc(doc, "ID", "FormaPago"));
                paymentTerms.appendChild(cbc(doc, "PaymentMeansID", "Contado"));
                invoice.appendChild(paymentTerms);
            }

            invoice.appendChild(buildTaxTotal(doc, c));
            invoice.appendChild(buildLegalMonetaryTotal(doc, c));

            int lineId = 1;
            for (ComprobanteDetalle item : c.getDetalles()) {
                invoice.appendChild(buildInvoiceLine(doc, item, lineId++));
            }

            return doc;
        } catch (Exception e) {
            throw new IllegalStateException("Error construyendo XML UBL del comprobante", e);
        }
    }

    private Element buildUblExtensions(Document doc) {
        Element extensions = ns(doc, NS_EXT, "ext:UBLExtensions");
        Element extension = ns(doc, NS_EXT, "ext:UBLExtension");
        Element content = ns(doc, NS_EXT, "ext:ExtensionContent");

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
        Element supplierParty = cac(doc, "AccountingSupplierParty");
        Element party = cac(doc, "Party");

        Element partyId = cac(doc, "PartyIdentification");
        Element id = cbc(doc, "ID", empresa.getRuc());
        id.setAttribute("schemeID", "6");
        partyId.appendChild(id);

        Element partyName = cac(doc, "PartyName");
        partyName.appendChild(cbc(doc, "Name", empresa.getNombreComercial() != null
                ? empresa.getNombreComercial() : empresa.getRazonSocial()));

        Element legalEntity = cac(doc, "PartyLegalEntity");
        legalEntity.appendChild(cbc(doc, "RegistrationName", empresa.getRazonSocial()));

        Element regAddress = cac(doc, "RegistrationAddress");
        Element ubigeoEl = cbc(doc, "ID", empresa.getUbigeo());
        ubigeoEl.setAttribute("schemeName", "Ubigeos");
        ubigeoEl.setAttribute("schemeAgencyName", "PE:INEI");
        regAddress.appendChild(ubigeoEl);

        Element addressTypeCode = cbc(doc, "AddressTypeCode", "0000");
        addressTypeCode.setAttribute("listAgencyName", "PE:SUNAT");
        addressTypeCode.setAttribute("listName", "Establecimientos anexos");
        regAddress.appendChild(addressTypeCode);

        regAddress.appendChild(cbc(doc, "CityName", empresa.getProvincia()));
        regAddress.appendChild(cbc(doc, "CountrySubentity", empresa.getDepartamento()));
        regAddress.appendChild(cbc(doc, "District", empresa.getDistrito()));

        Element addressLine = cac(doc, "AddressLine");
        addressLine.appendChild(cbc(doc, "Line", empresa.getDireccionFiscal()));
        regAddress.appendChild(addressLine);

        Element country = cac(doc, "Country");
        Element countryCode = cbc(doc, "IdentificationCode", "PE");
        countryCode.setAttribute("listID", "ISO 3166-1");
        countryCode.setAttribute("listAgencyName", "United Nations Economic Commission for Europe");
        countryCode.setAttribute("listName", "Country");
        country.appendChild(countryCode);
        regAddress.appendChild(country);
        legalEntity.appendChild(regAddress);

        party.appendChild(partyId);
        party.appendChild(partyName);
        party.appendChild(legalEntity);
        supplierParty.appendChild(party);
        return supplierParty;
    }

    private Element buildCustomerParty(Document doc, Comprobante c) {
        Element customerParty = cac(doc, "AccountingCustomerParty");
        Element party = cac(doc, "Party");

        Element partyId = cac(doc, "PartyIdentification");
        Element id = cbc(doc, "ID", c.getClienteNumeroDocumento());
        id.setAttribute("schemeID", c.getClienteTipoDocumento());
        id.setAttribute("schemeName", "Documento de Identidad");
        id.setAttribute("schemeAgencyName", "PE:SUNAT");
        id.setAttribute("schemeURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");
        partyId.appendChild(id);

        Element legalEntity = cac(doc, "PartyLegalEntity");
        legalEntity.appendChild(cbc(doc, "RegistrationName", c.getClienteNombre()));

        party.appendChild(partyId);
        party.appendChild(legalEntity);
        customerParty.appendChild(party);
        return customerParty;
    }

    private Element buildTaxTotal(Document doc, Comprobante c) {
        Element taxTotal = cac(doc, "TaxTotal");
        taxTotal.appendChild(cbcMoney(doc, "TaxAmount", c.getTotalIgv()));

        Element subtotal = cac(doc, "TaxSubtotal");
        subtotal.appendChild(cbcMoney(doc, "TaxableAmount", c.getTotalGravada()));
        subtotal.appendChild(cbcMoney(doc, "TaxAmount", c.getTotalIgv()));

        Element category = cac(doc, "TaxCategory");
        Element scheme = cac(doc, "TaxScheme");
        Element schemeId = cbc(doc, "ID", "1000");
        schemeId.setAttribute("schemeName", "Codigo de tributos");
        schemeId.setAttribute("schemeAgencyName", "PE:SUNAT");
        schemeId.setAttribute("schemeURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo05");
        scheme.appendChild(schemeId);
        scheme.appendChild(cbc(doc, "Name", "IGV"));
        scheme.appendChild(cbc(doc, "TaxTypeCode", "VAT"));
        category.appendChild(scheme);
        subtotal.appendChild(category);

        taxTotal.appendChild(subtotal);
        return taxTotal;
    }

    private Element buildLegalMonetaryTotal(Document doc, Comprobante c) {
        Element total = cac(doc, "LegalMonetaryTotal");
        total.appendChild(cbcMoney(doc, "LineExtensionAmount", c.getTotalGravada()));
        total.appendChild(cbcMoney(doc, "TaxInclusiveAmount", c.getTotalPagar()));
        total.appendChild(cbcMoney(doc, "AllowanceTotalAmount", BigDecimal.ZERO));
        total.appendChild(cbcMoney(doc, "ChargeTotalAmount", BigDecimal.ZERO));
        total.appendChild(cbcMoney(doc, "PrepaidAmount", BigDecimal.ZERO));
        total.appendChild(cbcMoney(doc, "PayableAmount", c.getTotalPagar()));
        return total;
    }

    private Element buildInvoiceLine(Document doc, ComprobanteDetalle item, int lineId) {
        Element line = cac(doc, "InvoiceLine");
        line.appendChild(cbc(doc, "ID", String.valueOf(lineId)));

        Element cantidad = cbc(doc, "InvoicedQuantity", item.getCantidad().toPlainString());
        cantidad.setAttribute("unitCode", "NIU");
        line.appendChild(cantidad);

        BigDecimal valorVenta = item.getValorVenta().setScale(2, RoundingMode.HALF_UP);
        line.appendChild(cbcMoney(doc, "LineExtensionAmount", valorVenta));

        BigDecimal precioConIgv = item.getPrecioUnitario()
                .multiply(BigDecimal.ONE.add(IGV_PORCENTAJE))
                .setScale(2, RoundingMode.HALF_UP);

        Element pricingRef = cac(doc, "PricingReference");
        Element altPrice = cac(doc, "AlternativeConditionPrice");
        altPrice.appendChild(cbcMoney(doc, "PriceAmount", precioConIgv));
        Element priceTypeCode = cbc(doc, "PriceTypeCode", "01");
        priceTypeCode.setAttribute("listName", "Tipo de Precio");
        priceTypeCode.setAttribute("listAgencyName", "PE:SUNAT");
        priceTypeCode.setAttribute("listURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo16");
        altPrice.appendChild(priceTypeCode);
        pricingRef.appendChild(altPrice);
        line.appendChild(pricingRef);

        BigDecimal igvItem = valorVenta.multiply(IGV_PORCENTAJE).setScale(2, RoundingMode.HALF_UP);
        Element taxTotal = cac(doc, "TaxTotal");
        taxTotal.appendChild(cbcMoney(doc, "TaxAmount", igvItem));
        Element subtotal = cac(doc, "TaxSubtotal");
        subtotal.appendChild(cbcMoney(doc, "TaxableAmount", valorVenta));
        subtotal.appendChild(cbcMoney(doc, "TaxAmount", igvItem));
        Element category = cac(doc, "TaxCategory");
        category.appendChild(cbc(doc, "Percent", "18"));
        Element exemptionCode = cbc(doc, "TaxExemptionReasonCode", "10");
        exemptionCode.setAttribute("listAgencyName", "PE:SUNAT");
        exemptionCode.setAttribute("listName", "Codigo de Tipo de Afectacion del IGV");
        exemptionCode.setAttribute("listURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo07");
        category.appendChild(exemptionCode);
        Element scheme = cac(doc, "TaxScheme");
        scheme.appendChild(cbc(doc, "ID", "1000"));
        scheme.appendChild(cbc(doc, "Name", "IGV"));
        scheme.appendChild(cbc(doc, "TaxTypeCode", "VAT"));
        category.appendChild(scheme);
        subtotal.appendChild(category);
        taxTotal.appendChild(subtotal);
        line.appendChild(taxTotal);

        Element itemEl = cac(doc, "Item");
        itemEl.appendChild(cbc(doc, "Description", item.getDescripcion()));
        if (item.getCodigoProductoSunat() != null && !item.getCodigoProductoSunat().isBlank()) {
            Element commodity = cac(doc, "CommodityClassification");
            commodity.appendChild(cbc(doc, "ItemClassificationCode", item.getCodigoProductoSunat()));
            itemEl.appendChild(commodity);
        }
        line.appendChild(itemEl);

        Element price = cac(doc, "Price");
        price.appendChild(cbcMoney(doc, "PriceAmount", item.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP)));
        line.appendChild(price);

        return line;
    }

    private Element ns(Document doc, String namespace, String localName) {
        return doc.createElementNS(namespace, localName);
    }

    private Element cac(Document doc, String localName) {
        return doc.createElementNS(NS_CAC, "cac:" + localName);
    }

    private Element cbc(Document doc, String localName, String value) {
        Element el = doc.createElementNS(NS_CBC, "cbc:" + localName);
        el.setTextContent(value);
        return el;
    }

    private Element cbcMoney(Document doc, String localName, BigDecimal amount) {
        Element el = cbc(doc, localName, amount.setScale(2, RoundingMode.HALF_UP).toPlainString());
        el.setAttribute("currencyID", "PEN");
        return el;
    }
}

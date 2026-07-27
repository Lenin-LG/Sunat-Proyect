package com.tuempresa.facturacion.infrastructure.adapters.output.xml;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.model.ComprobanteDetalle;
import com.tuempresa.facturacion.domain.model.Cuota;
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
    private static final String NS_CREDIT_NOTE = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2";
    private static final String NS_DEBIT_NOTE = "urn:oasis:names:specification:ubl:schema:xsd:DebitNote-2";
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

            String rootTag = "Invoice";
            String nsRoot = NS_INVOICE;
            if ("07".equals(c.getTipoDocumento())) {
                rootTag = "CreditNote";
                nsRoot = NS_CREDIT_NOTE;
            } else if ("08".equals(c.getTipoDocumento())) {
                rootTag = "DebitNote";
                nsRoot = NS_DEBIT_NOTE;
            }

            Element root = doc.createElementNS(nsRoot, rootTag);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", nsRoot);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cac", NS_CAC);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cbc", NS_CBC);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ext", NS_EXT);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ds", NS_DS);
            doc.appendChild(root);

            root.appendChild(buildUblExtensions(doc));
            root.appendChild(cbc(doc, "UBLVersionID", "2.1"));
            root.appendChild(cbc(doc, "CustomizationID", "2.0"));
            root.appendChild(cbc(doc, "ID", c.getSerie() + "-" + c.getNumero()));
            root.appendChild(cbc(doc, "IssueDate", c.getFechaEmision().toString()));
            root.appendChild(cbc(doc, "IssueTime", "00:00:00"));

            if ("01".equals(c.getTipoDocumento()) || "03".equals(c.getTipoDocumento())) {
                Element tipoDoc = cbc(doc, "InvoiceTypeCode", c.getTipoDocumento());
                tipoDoc.setAttribute("listID", "0101");
                tipoDoc.setAttribute("listAgencyName", "PE:SUNAT");
                tipoDoc.setAttribute("listName", "Tipo de Documento");
                tipoDoc.setAttribute("listURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo01");
                root.appendChild(tipoDoc);
            }

            Element note = cbc(doc, "Note", "IMPORTE EN LETRAS PENDIENTE DE CONVERSION");
            note.setAttribute("languageLocaleID", "1000");
            root.appendChild(note);

            Element moneda = cbc(doc, "DocumentCurrencyCode", "PEN");
            moneda.setAttribute("listID", "ISO 4217 Alpha");
            moneda.setAttribute("listName", "Currency");
            moneda.setAttribute("listAgencyName", "United Nations Economic Commission for Europe");
            root.appendChild(moneda);

            // Notas de Crédito / Débito: Referencia al documento afectado
            if ("07".equals(c.getTipoDocumento()) || "08".equals(c.getTipoDocumento())) {
                Element discrepancy = cac(doc, "DiscrepancyResponse");
                discrepancy.appendChild(cbc(doc, "ReferenceID", c.getDocumentoModificadoId()));
                discrepancy.appendChild(cbc(doc, "ResponseCode", c.getNotaMotivoCodigo()));
                discrepancy.appendChild(cbc(doc, "Description", c.getNotaMotivoDescripcion()));
                root.appendChild(discrepancy);

                Element billingRef = cac(doc, "BillingReference");
                Element docRef = cac(doc, "InvoiceDocumentReference");
                docRef.appendChild(cbc(doc, "ID", c.getDocumentoModificadoId()));
                docRef.appendChild(cbc(doc, "DocumentTypeCode", c.getDocumentoModificadoTipo()));
                billingRef.appendChild(docRef);
                root.appendChild(billingRef);
            }

            root.appendChild(buildFirmaNegocio(doc, empresa));
            root.appendChild(buildSupplierParty(doc, empresa));
            root.appendChild(buildCustomerParty(doc, c));

            // PaymentTerms (Contado / Crédito con Cuotas / Detracciones)
            if ("01".equals(c.getTipoDocumento()) || "03".equals(c.getTipoDocumento())) {
                if ("CREDITO".equalsIgnoreCase(c.getFormaPago())) {
                    Element ptForma = cac(doc, "PaymentTerms");
                    ptForma.appendChild(cbc(doc, "ID", "FormaPago"));
                    ptForma.appendChild(cbc(doc, "PaymentMeansID", "Credito"));
                    ptForma.appendChild(cbcMoney(doc, "Amount", c.getSaldoPendiente()));
                    root.appendChild(ptForma);

                    int cuotaIndex = 1;
                    for (Cuota cuota : c.getCuotas()) {
                        Element ptCuota = cac(doc, "PaymentTerms");
                        ptCuota.appendChild(cbc(doc, "ID", "FormaPago"));
                        ptCuota.appendChild(cbc(doc, "PaymentMeansID", "Cuota" + String.format("%03d", cuotaIndex++)));
                        ptCuota.appendChild(cbcMoney(doc, "Amount", cuota.getMonto()));
                        ptCuota.appendChild(cbc(doc, "PaymentDueDate", cuota.getFechaVencimiento().toString()));
                        root.appendChild(ptCuota);
                    }
                } else {
                    Element paymentTerms = cac(doc, "PaymentTerms");
                    paymentTerms.appendChild(cbc(doc, "ID", "FormaPago"));
                    paymentTerms.appendChild(cbc(doc, "PaymentMeansID", "Contado"));
                    root.appendChild(paymentTerms);
                }

                // Detracciones
                if (c.getDetraccionCodigo() != null && !c.getDetraccionCodigo().isBlank()) {
                    Element ptDetraccion = cac(doc, "PaymentTerms");
                    ptDetraccion.appendChild(cbc(doc, "ID", "Detraccion"));
                    ptDetraccion.appendChild(cbc(doc, "PaymentMeansID", c.getDetraccionCodigo()));
                    ptDetraccion.appendChild(cbc(doc, "PaymentPercent", c.getDetraccionPorcentaje().toPlainString()));
                    ptDetraccion.appendChild(cbcMoney(doc, "Amount", c.getDetraccionMonto()));
                    root.appendChild(ptDetraccion);
                }
            }

            root.appendChild(buildTaxTotal(doc, c));
            root.appendChild(buildLegalMonetaryTotal(doc, c));

            int lineId = 1;
            for (ComprobanteDetalle item : c.getDetalles()) {
                root.appendChild(buildLine(doc, item, lineId++, c.getTipoDocumento()));
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
        BigDecimal totalTax = c.getTotalIgv();
        if (c.getTotalImpuestoBolsa() != null) {
            totalTax = totalTax.add(c.getTotalImpuestoBolsa());
        }

        Element taxTotal = cac(doc, "TaxTotal");
        taxTotal.appendChild(cbcMoney(doc, "TaxAmount", totalTax));

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

        if (c.getTotalImpuestoBolsa() != null && c.getTotalImpuestoBolsa().compareTo(BigDecimal.ZERO) > 0) {
            Element subtotalBolsa = cac(doc, "TaxSubtotal");
            subtotalBolsa.appendChild(cbcMoney(doc, "TaxAmount", c.getTotalImpuestoBolsa()));

            Element catBolsa = cac(doc, "TaxCategory");
            Element schBolsa = cac(doc, "TaxScheme");
            Element schBolsaId = cbc(doc, "ID", "7152");
            schBolsaId.setAttribute("schemeName", "Codigo de tributos");
            schBolsaId.setAttribute("schemeAgencyName", "PE:SUNAT");
            schBolsaId.setAttribute("schemeURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo05");
            schBolsa.appendChild(schBolsaId);
            schBolsa.appendChild(cbc(doc, "Name", "ICBPER"));
            schBolsa.appendChild(cbc(doc, "TaxTypeCode", "OTH"));
            catBolsa.appendChild(schBolsa);
            subtotalBolsa.appendChild(catBolsa);
            taxTotal.appendChild(subtotalBolsa);
        }

        return taxTotal;
    }

    private Element buildLegalMonetaryTotal(Document doc, Comprobante c) {
        String monetaryTagName = "LegalMonetaryTotal";
        if ("08".equals(c.getTipoDocumento())) {
            monetaryTagName = "RequestedMonetaryTotal";
        }
        Element total = doc.createElementNS(NS_CAC, "cac:" + monetaryTagName);
        total.appendChild(cbcMoney(doc, "LineExtensionAmount", c.getTotalGravada()));
        total.appendChild(cbcMoney(doc, "TaxInclusiveAmount", c.getTotalPagar()));
        total.appendChild(cbcMoney(doc, "AllowanceTotalAmount", c.getDescuentoGlobal()));
        total.appendChild(cbcMoney(doc, "ChargeTotalAmount", BigDecimal.ZERO));
        total.appendChild(cbcMoney(doc, "PrepaidAmount", BigDecimal.ZERO));
        total.appendChild(cbcMoney(doc, "PayableAmount", c.getTotalPagar()));
        return total;
    }

    private Element buildLine(Document doc, ComprobanteDetalle item, int lineId, String tipoCpe) {
        String lineTag = "InvoiceLine";
        String qtyTag = "InvoicedQuantity";
        if ("07".equals(tipoCpe)) {
            lineTag = "CreditNoteLine";
            qtyTag = "CreditedQuantity";
        } else if ("08".equals(tipoCpe)) {
            lineTag = "DebitNoteLine";
            qtyTag = "DebitedQuantity";
        }

        Element line = cac(doc, lineTag);
        line.appendChild(cbc(doc, "ID", String.valueOf(lineId)));

        Element cantidad = cbc(doc, qtyTag, item.getCantidad().toPlainString());
        cantidad.setAttribute("unitCode", item.getTipoUnidad());
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
        BigDecimal bolsaAmount = BigDecimal.ZERO;
        if (item.getImpuestoBolsa() != null && item.getImpuestoBolsa().compareTo(BigDecimal.ZERO) > 0) {
            bolsaAmount = item.getImpuestoBolsa().multiply(item.getCantidad()).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal totalTaxItem = igvItem.add(bolsaAmount);

        Element taxTotal = cac(doc, "TaxTotal");
        taxTotal.appendChild(cbcMoney(doc, "TaxAmount", totalTaxItem));

        Element subtotal = cac(doc, "TaxSubtotal");
        subtotal.appendChild(cbcMoney(doc, "TaxableAmount", valorVenta));
        subtotal.appendChild(cbcMoney(doc, "TaxAmount", igvItem));
        Element category = cac(doc, "TaxCategory");
        category.appendChild(cbc(doc, "Percent", "18"));

        // SUNAT Catálogo 07 Afectación del IGV
        Element exemptionCode = cbc(doc, "TaxExemptionReasonCode", item.getTipoAfectacionIgv());
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

        if (bolsaAmount.compareTo(BigDecimal.ZERO) > 0) {
            Element subtotalBolsa = cac(doc, "TaxSubtotal");
            subtotalBolsa.appendChild(cbcMoney(doc, "TaxAmount", bolsaAmount));

            Element measure = cbc(doc, "BaseUnitMeasure", item.getCantidad().setScale(0, RoundingMode.HALF_UP).toPlainString());
            measure.setAttribute("unitCode", "NIU");
            subtotalBolsa.appendChild(measure);

            subtotalBolsa.appendChild(cbcMoney(doc, "PerUnitAmountRate", item.getImpuestoBolsa()));

            Element catBolsa = cac(doc, "TaxCategory");
            Element schBolsa = cac(doc, "TaxScheme");
            Element schBolsaId = cbc(doc, "ID", "7152");
            schBolsaId.setAttribute("schemeName", "Codigo de tributos");
            schBolsaId.setAttribute("schemeAgencyName", "PE:SUNAT");
            schBolsaId.setAttribute("schemeURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo05");
            schBolsa.appendChild(schBolsaId);
            schBolsa.appendChild(cbc(doc, "Name", "ICBPER"));
            schBolsa.appendChild(cbc(doc, "TaxTypeCode", "OTH"));
            catBolsa.appendChild(schBolsa);
            subtotalBolsa.appendChild(catBolsa);
            taxTotal.appendChild(subtotalBolsa);
        }
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

    private Element cbc(Document doc, String localName, String value, String ns) {
        Element el = doc.createElementNS(ns, "sac:" + localName);
        el.setTextContent(value);
        return el;
    }

    private Element cbcMoney(Document doc, String localName, BigDecimal amount) {
        Element el = cbc(doc, localName, amount.setScale(2, RoundingMode.HALF_UP).toPlainString());
        el.setAttribute("currencyID", "PEN");
        return el;
    }

    @Override
    public Document construirBaja(Comprobante c, Empresa empresa, String motivo, String idBaja) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().newDocument();

            String nsVoided = "urn:sunat:names:specification:ubl:peru:schema:xsd:VoidedDocuments-1";
            String nsSac = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1";

            Element root = doc.createElementNS(nsVoided, "VoidedDocuments");
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", nsVoided);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cac", NS_CAC);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cbc", NS_CBC);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:sac", nsSac);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ext", NS_EXT);
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ds", NS_DS);
            doc.appendChild(root);

            root.appendChild(buildUblExtensions(doc));
            root.appendChild(cbc(doc, "UBLVersionID", "2.0"));
            root.appendChild(cbc(doc, "CustomizationID", "1.0"));
            root.appendChild(cbc(doc, "ID", idBaja));
            root.appendChild(cbc(doc, "ReferenceDate", c.getFechaEmision().toString()));
            root.appendChild(cbc(doc, "IssueDate", java.time.LocalDate.now().toString()));

            root.appendChild(buildFirmaNegocio(doc, empresa));
            root.appendChild(buildSupplierPartyBaja(doc, empresa));

            Element line = doc.createElementNS(nsSac, "sac:VoidedDocumentsLine");
            line.appendChild(cbc(doc, "LineID", "1"));
            line.appendChild(cbc(doc, "DocumentTypeCode", c.getTipoDocumento()));
            line.appendChild(cbc(doc, "DocumentSerialID", c.getSerie(), nsSac));
            line.appendChild(cbc(doc, "DocumentNumberID", String.valueOf(c.getNumero()), nsSac));
            line.appendChild(cbc(doc, "VoidReasonDescription", motivo, nsSac));
            root.appendChild(line);

            return doc;
        } catch (Exception e) {
            throw new IllegalStateException("Error construyendo XML de Baja", e);
        }
    }

    private Element buildSupplierPartyBaja(Document doc, Empresa empresa) {
        Element supplierParty = cac(doc, "AccountingSupplierParty");
        supplierParty.appendChild(cbc(doc, "CustomerAssignedAccountID", empresa.getRuc()));
        supplierParty.appendChild(cbc(doc, "AdditionalAccountID", "6"));

        Element party = cac(doc, "Party");
        Element legalEntity = cac(doc, "PartyLegalEntity");
        legalEntity.appendChild(cbc(doc, "RegistrationName", empresa.getRazonSocial()));
        party.appendChild(legalEntity);
        supplierParty.appendChild(party);

        return supplierParty;
    }
}

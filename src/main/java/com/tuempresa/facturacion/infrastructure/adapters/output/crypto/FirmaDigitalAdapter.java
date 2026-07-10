package com.tuempresa.facturacion.infrastructure.adapters.output.crypto;

import com.tuempresa.facturacion.domain.ports.out.FirmaDigitalPort;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

@Service
public class FirmaDigitalAdapter implements FirmaDigitalPort {

    @Override
    public Document firmar(Document xmlDoc, PrivateKey privateKey, X509Certificate certificado) {
        try {
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

            Reference reference = fac.newReference(
                    "",
                    fac.newDigestMethod(DigestMethod.SHA1, null),
                    List.of(
                        fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null),
                        fac.newTransform(CanonicalizationMethod.EXCLUSIVE, (TransformParameterSpec) null)
                    ),
                    null, null);

            SignedInfo signedInfo = fac.newSignedInfo(
                    fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                    List.of(reference));

            KeyInfoFactory kif = fac.getKeyInfoFactory();
            X509Data x509Data = kif.newX509Data(List.of(certificado));
            KeyInfo keyInfo = kif.newKeyInfo(List.of(x509Data));

            Element extensionContent = buscarExtensionContent(xmlDoc);

            DOMSignContext signContext = new DOMSignContext(privateKey, extensionContent);
            XMLSignature signature = fac.newXMLSignature(signedInfo, keyInfo);
            signature.sign(signContext);

            return xmlDoc;
        } catch (Exception e) {
            throw new IllegalStateException("Error al firmar digitalmente el comprobante", e);
        }
    }

    private Element buscarExtensionContent(Document doc) {
        NodeList nodes = doc.getElementsByTagNameNS(
                "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2",
                "ExtensionContent");
        if (nodes.getLength() == 0) {
            throw new IllegalStateException("El XML no contiene ext:ExtensionContent donde insertar la firma");
        }
        return (Element) nodes.item(0);
    }
}

package com.tuempresa.facturacion.domain.ports.out;

import org.w3c.dom.Document;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public interface FirmaDigitalPort {
    Document firmar(Document xmlDoc, PrivateKey privateKey, X509Certificate certificado);
}

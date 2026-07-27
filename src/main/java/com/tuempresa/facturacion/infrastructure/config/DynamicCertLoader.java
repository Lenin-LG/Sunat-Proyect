package com.tuempresa.facturacion.infrastructure.config;

import com.tuempresa.facturacion.domain.model.Empresa;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class DynamicCertLoader {

    public static class CertKeys {
        public final PrivateKey privateKey;
        public final X509Certificate certificate;

        public CertKeys(PrivateKey privateKey, X509Certificate certificate) {
            this.privateKey = privateKey;
            this.certificate = certificate;
        }
    }

    public static CertKeys load(Empresa empresa, PrivateKey defaultKey, X509Certificate defaultCert) {
        if (empresa == null || empresa.getCertificadoBase64() == null || empresa.getCertificadoBase64().isBlank()) {
            return new CertKeys(defaultKey, defaultCert);
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(empresa.getCertificadoBase64().trim());
            String password = empresa.getCertificadoPassword();
            if (password == null) {
                password = "";
            }
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new ByteArrayInputStream(decoded), password.toCharArray());
            String alias = ks.aliases().nextElement();
            PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
            return new CertKeys(key, cert);
        } catch (Exception e) {
            throw new IllegalStateException("Error al cargar certificado dinamico para RUC: " + empresa.getRuc(), e);
        }
    }
}

package com.tuempresa.facturacion.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Configuration
public class CertificadoConfig {

    @Value("${sunat.certificado-path}")
    private String certificadoPath;

    @Value("${sunat.certificado-password}")
    private String certificadoPassword;

    @Bean
    public PrivateKey privateKey() throws Exception {
        return (PrivateKey) keyStore().getKey(alias(), certificadoPassword.toCharArray());
    }

    @Bean
    public X509Certificate certificadoX509() throws Exception {
        return (X509Certificate) keyStore().getCertificate(alias());
    }

    private KeyStore keyStore() throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(certificadoPath)) {
            ks.load(fis, certificadoPassword.toCharArray());
        }
        return ks;
    }

    private String alias() throws Exception {
        return keyStore().aliases().nextElement();
    }
}

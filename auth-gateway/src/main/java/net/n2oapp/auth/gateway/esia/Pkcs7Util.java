package net.n2oapp.auth.gateway.esia;

import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Компонент для формирования секрета при авторизации ч.з. ESIA
 * ru.rt.eu.n2o.control.rri.util.Pkcs7Util
 */

@Component
public final class Pkcs7Util {

    @Value("${access.esia.path-to-keystore}")
    private String pathToKeystore;

    @Value("${access.esia.key-alias}")
    private String keyAlias;

    @Value("${access.esia.key-store-password}")
    private String keyStorePassword;

    private static final String SIGNATURE_ALG = "SHA256withRSA";

    private CMSSignedDataGenerator generator;

    public String getUrlSafeSign(final String content) {
        try {
            byte[] signedBytes = signPkcs7(content.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getUrlEncoder().encode(signedBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private KeyStore loadKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(pathToKeystore);
        keystore.load(is, keyStorePassword.toCharArray());
        return keystore;
    }

    private CMSSignedDataGenerator setUpProvider(final KeyStore keystore) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Certificate[] certificateChain = keystore.getCertificateChain(keyAlias);
        final List<Certificate> certificates = new ArrayList<>();

        for (int i = 0, length = certificateChain == null ? 0 : certificateChain.length; i < length; i++) {
            certificates.add(certificateChain[i]);
        }

        Store store = new JcaCertStore(certificates);
        Certificate cert = keystore.getCertificate(keyAlias);
        ContentSigner signer = new JcaContentSignerBuilder(SIGNATURE_ALG).setProvider("BC").
                build((PrivateKey) (keystore.getKey(keyAlias, keyStorePassword.toCharArray())));

        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").
                build()).build(signer, (X509Certificate) cert));

        generator.addCertificates(store);

        return generator;
    }

    private byte[] signPkcs7(final byte[] content) throws Exception {
        if (generator == null) {
            generator = setUpProvider(loadKeyStore());
        }
        return generator.generate(new CMSProcessableByteArray(content), true).getEncoded();
    }
}
package net.n2oapp.security.admin.auth.server.esia;

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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Компонент для формирования секрета при авторизации ч.з. ESIA
 * ru.rt.eu.n2o.control.rri.util.Pkcs7Util
 */
@Component
public final class Pkcs7Util {

    @Value("${access.esia.path-to-keystore:#{null}}")
    private Resource pathToKeystore;

    @Value("${access.esia.key-alias:#{null}}")
    private String keyAlias;

    @Value("${access.esia.key-store-password:#{null}}")
    private String keyStorePassword;

    @Value("${access.esia.signing-key:#{null}}")
    private String signingKey;

    @Value("${access.esia.certificate:#{null}}")
    private String certificate;

    private static final String SIGNATURE_ALG = "SHA256withRSA";

    private static final String DEFAULT_KEY_ALIAS = "default_key_alias";

    private CMSSignedDataGenerator generator;

    public String getUrlSafeSign(final String content) {
        try {
            byte[] signedBytes = signPkcs7(content.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getUrlEncoder().encode(signedBytes));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private KeyStore loadKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        if (nonNull(pathToKeystore)) {
            InputStream is = pathToKeystore.getInputStream();
            keystore.load(is, keyStorePassword.toCharArray());
        } else {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            formatCertificate();
            Certificate compiledCertificate = cf.generateCertificate(new ByteArrayInputStream(certificate.getBytes(StandardCharsets.UTF_8)));
            keystore.load(null, null);
            keyAlias = DEFAULT_KEY_ALIAS;
            keyStorePassword = keyAlias;
            keystore.setCertificateEntry(keyAlias, compiledCertificate);
            keystore.setKeyEntry(keyAlias, privateKeyFromPem(), keyStorePassword.toCharArray(), new Certificate[]{compiledCertificate});
        }

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

        CMSSignedDataGenerator result = new CMSSignedDataGenerator();
        result.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").
                build()).build(signer, (X509Certificate) cert));

        result.addCertificates(store);
        return result;
    }

    private byte[] signPkcs7(final byte[] content) throws Exception {
        if (generator == null) {
            generator = setUpProvider(loadKeyStore());
        }
        return generator.generate(new CMSProcessableByteArray(content), true).getEncoded();
    }

    private PrivateKey privateKeyFromPem() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyContent = signingKey.strip();
        privateKeyContent = privateKeyContent.replace("\n", "").replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent), "RSA");
        return kf.generatePrivate(keySpecPKCS8);
    }

    private void formatCertificate() {
        certificate = certificate.replace("\n", "").replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "");
        certificate = "-----BEGIN CERTIFICATE-----\n" + certificate + "\n-----END CERTIFICATE-----";
    }
}
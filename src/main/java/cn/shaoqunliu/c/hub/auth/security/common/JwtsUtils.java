package cn.shaoqunliu.c.hub.auth.security.common;

import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

public class JwtsUtils {

    private static RSAPrivateKey privateKey;
    private static String x5cCertificate;

    public static void init() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        // get public & private key
        String pkcs8Path = System.getenv("PKCS8_PATH");
        String certPath = System.getenv("CERT_PATH");
        // get Https SSL RSA private key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(getByteArrayFromFile(pkcs8Path));
        privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        // get x5c standardized certification
        x5cCertificate = Base64.getEncoder().encodeToString(getByteArrayFromFile(certPath));
    }

    public static JwtBuilder getBuilder() {
        Date issuedAt = new Date();
        List<String> x5c = new ArrayList<>();
        x5c.add(x5cCertificate);
        return Jwts.builder()
                .setHeaderParam(JwsHeader.ALGORITHM, "JWT")
                // https://tools.ietf.org/html/rfc7515#section-4.1.6
                .setHeaderParam(JwsHeader.X509_CERT_CHAIN, x5c)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .setIssuer("A certain powerful developer surnamed Liu")
                .setAudience("A docker registry developed by a sane developer - Shaoqun Liu")
                .setExpiration(new Date(issuedAt.getTime() + 48 * 3600 * 1000))
                .setIssuedAt(issuedAt)
                .setId(UUID.randomUUID().toString());
    }

    private static byte[] getByteArrayFromFile(String path) throws IOException {
        File file = new File(path);
        FileInputStream in = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        in.read(bytes);
        return bytes;
    }
}

package eu.tsalliance.auth.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptUtil {

    protected static File secretsDir = new File(System.getProperty("user.dir") + File.separator + "secret");
    protected static File jwtSecretFile = new File(secretsDir.getAbsolutePath(), "jwt.secret");

    protected static SecretKey jwtSecretKey;

    public static SecretKey getJwtSecretKey() {
        try {
            if (!jwtSecretFile.exists()) {
                Files.createDirectories(secretsDir.toPath());
                Files.createFile(jwtSecretFile.toPath());

                SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
                String base64Key = Encoders.BASE64.encode(key.getEncoded());

                if(!writeSecretToFile(base64Key, jwtSecretFile)) {
                    throw new Exception("Could not write jwt secret. This may cause sessions being invalidated on each restart of the service.");
                }
            }

            if(jwtSecretKey == null) {
                jwtSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(readSecretFromFile(jwtSecretFile)));
            }

            return jwtSecretKey;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static boolean writeSecretToFile(String secret, File destination) {
        try {

            FileWriter writer = new FileWriter(destination);
            writer.write(secret);
            writer.flush();
            writer.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String readSecretFromFile(File input) {
        try {
            return Files.readString(input.toPath());
        } catch (Exception ex){
            ex.printStackTrace();
            return "";
        }
    }

}

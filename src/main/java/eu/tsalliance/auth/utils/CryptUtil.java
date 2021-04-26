package eu.tsalliance.auth.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptUtil {

    protected static File secretsDir = new File(System.getProperty("user.dir") + File.separator + "secret");
    protected static File jwtSecretFile = new File(secretsDir.getAbsolutePath(), "jwt.secret");

    protected static String jwtSecret;

    public static String generateAESSecret(){
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);

            SecretKey secretKey = generator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getJwtSecretKey() {
        try {
            if (!jwtSecretFile.exists()) {
                Files.createDirectories(secretsDir.toPath());
                Files.createFile(jwtSecretFile.toPath());

                if(!writeSecretToFile(generateAESSecret(), jwtSecretFile)) {
                    throw new Exception("Could not write jwt secret. This may cause sessions being invalidated on each restart of the service.");
                }
            }

            if(jwtSecret == null) {
                jwtSecret = readSecretFromFile(jwtSecretFile);
            }

            return jwtSecret;
        } catch (Exception ex) {
            return "";
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

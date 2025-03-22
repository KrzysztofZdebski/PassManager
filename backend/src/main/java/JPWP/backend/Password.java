package JPWP.backend;

// Java program to demonstrate the creation 
// of Encryption and Decryption with Java AES 
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec; 
import java.util.Base64; 
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey; 
import javax.crypto.SecretKeyFactory; 
import javax.crypto.spec.IvParameterSpec; 
import javax.crypto.spec.PBEKeySpec; 
import javax.crypto.spec.SecretKeySpec; 

public class Password {
    private static final String SECRET_KEY = "my_super_secret_key_ho_ho_ho";
    private static final String SALT = "ssshhhhhhhhhhh!!!!";
    private String pass;
    private Site site;
    public Password(String pass, Site site){
        this.site = site;
        this.pass = encrypt(pass);
    }

    private static byte[] ivGenerate(){
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
    
    private static SecretKey generateAESKey() throws Exception{
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(),SALT.getBytes(),65536,256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(),"AES"); 
    }

	// This method use to encrypt to string 
    public static String encrypt(String encryptedString) {
        try {
            byte[] iv = ivGenerate();
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKey secretKey = generateAESKey();
    
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
    
            byte[] encryptedBytes = cipher.doFinal(encryptedString.getBytes(StandardCharsets.UTF_8));

            byte[] encryptedWithIV = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, encryptedWithIV, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, encryptedWithIV, iv.length, encryptedBytes.length);
    
            return Base64.getEncoder().encodeToString(encryptedWithIV);
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
    
    public static String decrypt(String encryptedString) {
        try {
            byte[] encryptedWithIV = Base64.getDecoder().decode(encryptedString);
            if (encryptedWithIV.length < 17){
                System.out.println("Invalid encrypted data format");
                return null;
            }
            byte[] iv = new byte[16];
            System.arraycopy(encryptedWithIV, 0, iv, 0, iv.length);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
    
         
            byte[] encryptedBytes = new byte[encryptedWithIV.length - iv.length];
            System.arraycopy(encryptedWithIV, iv.length, encryptedBytes, 0, encryptedBytes.length);
    
            SecretKey secretKey = generateAESKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
    
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
    public String getEncryptedPassword(){
        return pass;
    }
    public String getPassword(){
        return decrypt(pass);
    }
    public void setPassword(String newPass){
        this.pass = encrypt(newPass);
    }
    public Site getSite(){
        return site;
    }
    public static void main(String[] args) {
        // try {
        //     String originalText = "Hello, World!";
        //     System.out.println("Original: " + originalText);

        //     String encryptedText = encrypt(originalText);
        //     System.out.println("Encrypted: " + encryptedText);

        //     String decryptedText = decrypt(encryptedText);
        //     System.out.println("Decrypted: " + decryptedText);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }
} 

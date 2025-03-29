package JPWP.backend;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec; 
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey; 
import javax.crypto.SecretKeyFactory; 
import javax.crypto.spec.IvParameterSpec; 
import javax.crypto.spec.PBEKeySpec; 
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName; 

public final class Password {
    private  final String SECRET_KEY = "my_super_secret_key_ho_ho_ho";
    private  final String SALT = "ssshhhhhhhhhhh!!!!";
    private  String key = new String();
    @SerializedName("encryptedPassword")
    private String pass;
    private Site site;    
    private String user;

    public Password(){

    }

    public Password(String pass, Site site, String user){
        this.site = site;
        this.key = generateKey();
        this.pass = encryptWithKey(pass,key);
        this.user = user;
    }

    private static byte[] ivGenerate(){
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
    
    private  String generateKey(){
        Random rand = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder generatedKey  = new StringBuilder();
        for(int i = 0;i < 64;i++){
            int index = rand.nextInt(0,characters.length());
            generatedKey.append(characters.charAt(index));
        }
        return generatedKey.toString();
    }

    private  SecretKey generateAESKey(boolean isEncrypted) throws Exception{
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        if(isEncrypted == false){key = generateKey();}
        KeySpec spec = new PBEKeySpec(key.toCharArray(),SALT.getBytes(),65536,256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(),"AES"); 
    }

    private  SecretKey getSecretKey(String key) throws Exception{
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(key.toCharArray(),SALT.getBytes(),65536,256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(),"AES"); 
    }

    public String generatePassword(String options){
        Random rand = new Random();
        int length = rand.nextInt(10,16);
        options = "skibidi";
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder generatedPassword = new StringBuilder();
        for(int i = 0; i < length; i++){
            int index = rand.nextInt(characters.length());
            generatedPassword.append((characters.charAt(index)));
        }
        return generatedPassword.toString();
    }

	// This method use to encrypt to string 
    public  String encrypt(String encryptedString) {
        try {
            byte[] iv = ivGenerate();
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKey secretKey = generateAESKey(false);
    
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
    
    public  String decrypt(String encryptedString) {
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
    
            SecretKey secretKey = generateAESKey(true);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
    
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
    @JsonProperty("encryptedPassword")
    public String getEncryptedPassword(){
        return pass;
    }
    @JsonProperty("encryptedPassword")
    public void setEncyptedPassword(String pass){
        this.pass = pass;
    }
    @JsonIgnore
    public String getPassword(String usrKey){
        return decryptedWithKey(pass,usrKey);
    }
    public void setPassword(String newPass){
        this.pass = encrypt(newPass);
    }
    public Site getSite(){
        return site;
    }
    public void setSite(Site site){
        this.site = site;
    }
    public String getKey(){
        return key;
    }
    public String getUser(){
        return user;
    }
    public void setUser(String user){
        this.user = user;
    }
    public String encryptWithKey(String password, String userKey){
        try{
            byte[] iv = ivGenerate();
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKey secretKey = getSecretKey(userKey);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            
            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedWithIV = new byte[iv.length + encryptedBytes.length];
            
            System.arraycopy(iv, 0, encryptedWithIV, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, encryptedWithIV, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(encryptedWithIV);
        }catch(Exception e){
            System.out.println("Error while encrypting with user key: " + e.toString());
        }
        return null;
    }
    public String decryptedWithKey(String encryptedPassword, String userKey){
        System.out.println("decrypting " + encryptedPassword + " with key " + userKey);
        try {
            byte[] encryptedWithIV = Base64.getDecoder().decode(encryptedPassword);
            if (encryptedWithIV.length < 17){
                System.out.println("Invalid encrypted data format");
                return null;
            }
            byte[] iv = new byte[16];
            System.arraycopy(encryptedWithIV, 0, iv, 0, iv.length);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
    
         
            byte[] encryptedBytes = new byte[encryptedWithIV.length - iv.length];
            System.arraycopy(encryptedWithIV, iv.length, encryptedBytes, 0, encryptedBytes.length);
    
            SecretKey secretKey = getSecretKey(userKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
    
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
 
} 
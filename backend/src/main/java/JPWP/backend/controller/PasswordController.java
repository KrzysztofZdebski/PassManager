package JPWP.backend.controller;

import java.util.HashMap;
import java.util.Map;
import JPWP.backend.*;
import java.io.File;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/passwords")
public class PasswordController {
    
    private Map<String, Password> passwordStore = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    public PasswordController(){
        loadData();
    }

    @PostMapping("/save")
    public ResponseEntity<String> savePassword(@RequestParam String siteName, @RequestParam String passwordName) {
        Site site = new Site(siteName);
        Password password = new Password(passwordName, site);
        String key = password.getKey();
        passwordStore.put(site.getNameSite(), password);
        saveToFile();
        System.out.println("Password saved!");
        return ResponseEntity.ok(key);
    }

    @GetMapping("/get")
    public ResponseEntity<String> getPassword(@RequestParam String siteName, @RequestParam String key) {
        Password getPassword = passwordStore.get(siteName);
        if(getPassword == null){
            return ResponseEntity.ok("Not found");
        }
        return ResponseEntity.ok(getPassword.getPassword(key));
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, String>> getAllPasswords() {
        Map<String, String> passwords = new HashMap<>();
        
        for (Map.Entry<String, Password> entry : passwordStore.entrySet()) {
            passwords.put(entry.getKey(),entry.getValue().getEncryptedPassword());   
        }
        return ResponseEntity.ok(passwords);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removePassword(@RequestParam String siteName){
        if (passwordStore.containsKey(siteName)) {
            passwordStore.remove(siteName);
            saveToFile();
            return ResponseEntity.ok("Password removed");
        }else{
            return ResponseEntity.ok("Password not");
        }
    }

    public void saveToFile(){
        try{
            objectMapper.writeValue(new File("src\\main\\java\\JPWP\\backend\\database\\passwords.json"),passwordStore);
        }catch(IOException e){
            System.out.println("Error saving passwords: "+ e);
        }
    }

    public void loadData(){
        try{ 
            File file = new File("src\\main\\java\\JPWP\\backend\\database\\passwords.json");
            if (file.exists() && file.length()>0) {
                passwordStore = objectMapper.readValue(file, objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class,Password.class ));
            }else{
                System.out.println("No existing password file found. Creating a new one.");
                passwordStore = new HashMap<>();
            }
        
        }catch(IOException e){
            System.out.println("Error loading passwords: "+ e);
            passwordStore = new HashMap<>();
        }
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generate(@RequestParam String options) {
        Password password = new Password();
        String generatedPassword = password.generatePassword(options);
        System.out.println("Generating password: "+ generatedPassword +" with options: " + options);
        return ResponseEntity.ok(generatedPassword);
    }
    
    @GetMapping("/encrypt")
    public String encryptPassword(@RequestParam String password, @RequestParam String key) {
        System.out.println("Encrypting password: " + password + " with key: " + key);
        Password pass = new Password();
        password = pass.encryptWithKey(password, key);
        return password + " encrypted";
    }

    @GetMapping("/decrypt")
    public String decryptPassword(@RequestParam String encryptedPassword, @RequestParam String key) {
        System.out.println("Decrypting password: " + encryptedPassword + " with key: " + key);
        Password pass = new Password();
        encryptedPassword = pass.decryptedWithKey(encryptedPassword, key);
        return encryptedPassword + " decrypted";
    }
}
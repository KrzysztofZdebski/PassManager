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
        passwordStore.put(site.getNameSite(), password);
        saveToFile();
        return ResponseEntity.ok("Password saved!");
    }

    @GetMapping("/get")
    public ResponseEntity<String> getPassword(@RequestParam String siteName) {
        Password password = passwordStore.get(siteName);
        if(password == null){
            return ResponseEntity.ok("Not found");
        }
        return ResponseEntity.ok(password.getPassword());
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
        System.out.println("Generating password with options: " + options);
        return ResponseEntity.ok("generated");
    }
    
    @GetMapping("/encrypt")
    public String encryptPassword(@RequestParam String password, @RequestParam String key) {
        System.out.println("Encrypting password: " + password + " with key: " + key);
        return password + " encrypted";
    }

    @GetMapping("/decrypt")
    public String decryptPassword(@RequestParam String password, @RequestParam String key) {
        System.out.println("Decrypting password: " + password + " with key: " + key);
        return password + " decrypted";
    }
}

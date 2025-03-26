package JPWP.backend.controller;

import java.util.ArrayList;
import java.util.stream.Stream;

import JPWP.backend.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
public final class PasswordController {
    
    private final String dataPath = "src\\main\\java\\JPWP\\backend\\database\\passwords.json";
    // private Map<String, Password> passwordStore = new HashMap<>();
    private List<Password> passwordStore;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public PasswordController(){
        loadData();
    }

    @PostMapping("/save")
    public ResponseEntity<String> savePassword(@RequestParam String siteName, @RequestParam String passwordName, @RequestParam String user) {
        Site site = new Site(siteName);
        Password password = new Password(passwordName, site, user);
        String key = password.getKey();
        passwordStore.add(password);
        saveToFile();
        System.out.println("Password saved!");
        return ResponseEntity.ok(key);
    }

    @GetMapping("/get")
    public ResponseEntity<String> getPassword(@RequestParam String siteName, @RequestParam String key, @RequestParam String user) {
        Password pass = passwordStore.parallelStream()
                                    .filter(p -> p.getSite().getNameSite().equals(siteName) && p.getUser().equals(user))
                                    .findFirst()
                                    .orElse(null);
        // Password getPassword = passwordStore.get(siteName);
        // if(getPassword == null){
        //     return ResponseEntity.ok("Not found");
        // }
        // return ResponseEntity.ok(getPassword.getPassword());
        if (pass == null) {
            return ResponseEntity.ok("Password not found");
        }
        return ResponseEntity.ok(pass.getPassword());
    }


    @GetMapping("/all")
    public ResponseEntity<List<Password>> getAllPasswords() {
        // Map<String, String> passwords = new HashMap<>();
        
        // for (Map.Entry<String, Password> entry : passwordStore.entrySet()) {
        //     passwords.put(entry.getKey(),entry.getValue().getEncryptedPassword());   
        // }
        // return ResponseEntity.ok(passwordStore);
        return ResponseEntity.ok(passwordStore);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removePassword(@RequestParam String siteName, @RequestParam String user) {
        System.out.println("Attempting to delete siteName: " + siteName + " From user " + user); // Debug log
        Password pass = passwordStore.parallelStream()
                                    .filter(p -> p.getSite().getNameSite().equals(siteName) && p.getUser().equals(user))
                                    .findFirst()
                                    .orElse(null);
        if (pass == null) {
            System.out.println("Password not found");
            return ResponseEntity.ok("Password not found");
        }
        passwordStore.remove(pass);
        saveToFile();
        System.out.println("Password removed");
        return ResponseEntity.ok("Password removed");
        // System.out.println("Attempting to delete siteName: " + siteName); // Debug log
        // if (passwordStore.containsKey(siteName)) {
        //     passwordStore.remove(siteName);
        //     saveToFile();
        //     return ResponseEntity.ok("Password removed");
        // } else {
        //     return ResponseEntity.ok("Password not found");
        // }
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generate(@RequestParam String options) {
        Password password = new Password();
        String generatedPassword = password.generatePassword(options);
        return ResponseEntity.ok(generatedPassword);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Boolean> authenticate(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(true);
    }

    public void saveToFile(){
        try{
            objectMapper.writeValue(new File(dataPath),passwordStore);
        }catch(IOException e){
            System.out.println("Error saving passwords: "+ e);
        }
    }

    public void loadData(){
        try{ 
            File file = new File(dataPath);
            if (file.exists() && file.length()>0) {
                passwordStore = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Password.class ));
            }else{
                System.out.println("No existing password file found. Creating a new one.");
                passwordStore = new ArrayList<>();
            }
        
        }catch(IOException e){
            System.out.println("Error loading passwords: "+ e);
            passwordStore = new ArrayList<>();
        }
    }

    public static Stream<Password> findByUser(List<Password> entries, String userName) {
        return entries.stream().filter(entry -> entry.getUser().equals(userName));
    }

    
    
    // @GetMapping("/encrypt")
    // public ResponseEntity<String> encryptPassword(@RequestParam String password, @RequestParam String key) {
    //     Password pass = new Password();
    //     System.out.println("encrypting " + password + " with key " + key);
    //     password = pass.encryptWithKey(password, key);
    //     System.out.println(password);
    //     return ResponseEntity.ok(password);
    // }

    // @GetMapping("/decrypt")
    // public ResponseEntity<String> decryptPassword(@RequestParam String encryptedPassword, @RequestParam String key) {
    //     Password pass = new Password();
    //     System.out.println("decrypting " + encryptedPassword + " with key " + key);
    //     encryptedPassword = pass.decryptedWithKey(encryptedPassword, key);
    //     System.out.println(encryptedPassword);
    //     return ResponseEntity.ok(encryptedPassword);
    // }
}
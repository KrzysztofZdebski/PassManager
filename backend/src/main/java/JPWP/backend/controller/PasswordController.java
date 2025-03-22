package JPWP.backend.controller;

import java.util.HashMap;
import java.util.Map;
import JPWP.backend.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/passwords")
public class PasswordController {
    
    private final Map<String, Password> passwordStore = new HashMap<>();
    @PostMapping("/save")
    public ResponseEntity<String> savePassword(@RequestParam String siteName, @RequestParam String passwordName) {
        Site site = new Site(siteName);
        Password password = new Password(passwordName, site);
        passwordStore.put(site.getNameSite(), password);
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
    public ResponseEntity<Map<String, String>> getAllPasswords(@RequestParam(defaultValue = "false") boolean decrypt) {
        Map<String, String> passwords = new HashMap<>();
        
        for (Map.Entry<String, Password> entry : passwordStore.entrySet()) {
            if (decrypt) {
                passwords.put(entry.getKey(), entry.getValue().getPassword());  
            } else {
                passwords.put(entry.getKey(), entry.getValue().getEncryptedPassword());  
            }
        }
        return ResponseEntity.ok(passwords);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removePassword(@RequestParam String siteName){
        if (passwordStore.containsKey(siteName)) {
            passwordStore.remove(siteName);
            return ResponseEntity.ok("Password removed");
        }else{
            return ResponseEntity.ok("Password not");
        }
    }
}

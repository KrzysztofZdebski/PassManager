package JPWP.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/passwords")
public class PasswordController {
    
    private final Map<String, String> passwordStore = new HashMap<>();

    @PostMapping("/save")
    public ResponseEntity<String> savePassword(@RequestParam String site, @RequestParam String password) {
        passwordStore.put(site, password);
        System.out.println("Saving password for site: " + site);
        return ResponseEntity.ok("Password saved!");
    }

    @GetMapping("/get")
    public ResponseEntity<String> getPassword(@RequestParam String site) {
        System.out.println("Getting password for site: " + site);
        return ResponseEntity.ok(passwordStore.getOrDefault(site, "Not found"));
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, String>> getAllPasswords() {
        return ResponseEntity.ok(passwordStore);
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

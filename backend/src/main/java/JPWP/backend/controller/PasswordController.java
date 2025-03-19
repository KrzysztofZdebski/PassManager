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
        return ResponseEntity.ok("Password saved!");
    }

    @GetMapping("/get")
    public ResponseEntity<String> getPassword(@RequestParam String site) {
        return ResponseEntity.ok(passwordStore.getOrDefault(site, "Not found"));
    }
}

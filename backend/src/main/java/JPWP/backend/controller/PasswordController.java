package JPWP.backend.controller;

import java.util.stream.Stream;

import JPWP.backend.*;
import JPWP.backend.fileHandler.*;

import java.util.Iterator;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/passwords")
public final class PasswordController {
    
    private final String dataPath = "src\\main\\java\\JPWP\\backend\\database\\large_passwords.json";
    private final String userPath = "src\\main\\java\\JPWP\\backend\\database\\users.json";
    // private final Object fileLock = new Object();

    // private Map<String, Password> passwordStore = new HashMap<>();
    private List<Password> passwordStore;
    public PasswordController(){
        // loadData();
    }

    @PostMapping("/save")
    public ResponseEntity<String> savePassword(@RequestParam String siteName, @RequestParam String passwordName, @RequestParam String user) {
        Site site = new Site(siteName);
        Password password = new Password(passwordName, site, user);
        String key = password.getKey();
        DataWriter dataWriter = new DataWriter();

        boolean updated;
        updated = dataWriter.updatePassword(dataPath, siteName, user, password);
    
        if (updated) {
            return ResponseEntity.ok(key); // Send response only after file operations are complete
        } else {
            return ResponseEntity.ok(key);
        }

    }

    @GetMapping("/get")
    public ResponseEntity<String> getPassword(@RequestParam String siteName, @RequestParam String key, @RequestParam String user) {
        // Password pass = passwordStore.parallelStream()
        try (Stream<Password> passwordStream = LazyLoader.loadPasswords(dataPath)) {
            Password pass = passwordStream
                                .filter(p -> p.getSite().getNameSite().equals(siteName) && p.getUser().equals(user))
                                .findFirst()
                                .orElse(null);
    
            if (pass == null) {
                return ResponseEntity.ok("Password not found");
            }
            return ResponseEntity.ok(pass.getPassword(key));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred while retrieving the password.");
        }
        // Password getPassword = passwordStore.get(siteName);
        // if(getPassword == null){
        //     return ResponseEntity.ok("Not found");
        // }
        // return ResponseEntity.ok(getPassword.getPassword());
        
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
        DataWriter dataWriter = new DataWriter();
        boolean removed;
        removed = dataWriter.removePassword(dataPath, siteName, user);

        if (removed) {
            // System.out.println("Password removed");
            return ResponseEntity.ok("Password removed");
        } else {
            // System.out.println("Password not found");
            return ResponseEntity.ok("Password not found");
        }
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generate(@RequestParam String options) {
        Password password = new Password();
        String generatedPassword = password.generatePassword(options);
        return ResponseEntity.ok(generatedPassword);
    }

    @PostMapping("/authenticate/login")
    public ResponseEntity<Boolean> authenticate(@RequestParam String username, @RequestParam String password) {
        try{
            Iterator<User> userIterator = LazyLoader.loadUsers(userPath);
            boolean isAuthenticated = false;
            while (userIterator.hasNext()) {
                User user = userIterator.next();
                if (user.userName().equals(username) && user.password().equals(password)) {
                    isAuthenticated = true;
                    break;
                }
            }
            // boolean isAuthenticated = userStream
            //                             .filter(u -> u.userName().equals(username) && u.password().equals(password))
            //                             .findFirst()
            //                             .isPresent();
            return ResponseEntity.ok(isAuthenticated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(false);
        }
    }

    @PostMapping("/authenticate/register")
    public ResponseEntity<String> registerUser(@RequestParam String username, @RequestParam String password) {
        boolean registered = DataWriter.addUser(userPath, username, password);
        if (!registered) {
            return ResponseEntity.status(400).body("User already exists!");
        }
        return ResponseEntity.ok("User registered successfully!");
    }

    // @GetMapping("/sync")
    // public List<Password> getMethodName(@RequestParam String user) {
    //     Stream<Password> passwordStream = LazyLoader.loadPasswords(dataPath);
    //     List<Password> passwords = passwordStream.filter(p -> p.getUser().equals(user))
    //                                              .toList();
                                                 
    //     return passwords;
    // }
    
    

    public void saveToFile(){
        // try{
        //     objectMapper.writeValue(new File(dataPath),passwordStore);
        // }catch(IOException e){
        //     System.out.println("Error saving passwords: "+ e);
        // }
    }

    // public void loadData(){
    //     try{ 
    //         File file = new File(dataPath);
    //         if (file.exists() && file.length()>0) {
    //             passwordStore = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Password.class ));
    //         }else{
    //             System.out.println("No existing password file found. Creating a new one.");
    //             passwordStore = new ArrayList<>();
    //         }
        
    //     }catch(IOException e){
    //         System.out.println("Error loading passwords: "+ e);
    //         passwordStore = new ArrayList<>();
    //     }
    // }



    
    
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
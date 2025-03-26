package JPWP.backend.fileHandler;

import java.io.FileReader;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import JPWP.backend.Password;

public class LazyLoader {
    private static final Gson gson = new Gson();

    public static Stream<Password> loadPasswords(String filePath) {
        try {
            // Create a JsonReader to read the JSON file incrementally
            JsonReader jsonReader = new JsonReader(new FileReader(filePath));

            // Ensure the JSON starts with an array
            jsonReader.beginArray();

            // Create an iterator to lazily parse each Password object
            Iterable<Password> iterable = () -> new Iterator<>() {
                @Override
                public boolean hasNext() {
                    try {
                        return jsonReader.hasNext();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                public Password next() {
                    try {
                        Password password = gson.fromJson(jsonReader, Password.class);
                        // Debug log to check the loaded Password object
                        // System.out.println("Loaded Password: " + password);
                        // System.out.println("Key: " + password.getKey());
                        // System.out.println("Encrypted Password: " + password.getEncryptedPassword());
                        // System.out.println("User: " + password.getUser());
                        return password;
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing JSON", e);
                    }
                }
            };

            // Convert the iterator to a Stream
            return StreamSupport.stream(iterable.spliterator(), false)
                                .onClose(() -> {
                                    try {
                                        jsonReader.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });

        } catch (Exception e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

    // public static void main(String[] args) {
    //     loadPasswords("C:\\Users\\fangi\\Desktop\\AGH\\JPWP\\PassManager\\backend\\src\\main\\java\\JPWP\\backend\\database\\large_passwords.json")
    //         .map(p -> p.getSite().getNameSite())
    //         .forEach(System.out::println);
    // }
}
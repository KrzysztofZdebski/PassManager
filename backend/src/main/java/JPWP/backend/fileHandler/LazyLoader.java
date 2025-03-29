package JPWP.backend.fileHandler;

import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import JPWP.backend.Password;
import JPWP.backend.User;

public class LazyLoader {
    private static final Gson gson = new Gson();

    public static Stream<Password> loadPasswords(String filePath) {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(filePath));
            jsonReader.beginArray(); // Start reading the JSON array

            Iterable<Password> iterable = () -> new Iterator<>() {
                private String currentUser = null;
                private Iterator<Password> passwordIterator = null;

                @Override
                public boolean hasNext() {
                    try {
                        // If the current password iterator is null or exhausted, load the next user
                        while ((passwordIterator == null || !passwordIterator.hasNext()) && jsonReader.hasNext()) {
                            // Read the next user entry as a Map<String, List<Password>>
                            Map<String, List<Password>> userPasswords = gson.fromJson(
                                jsonReader, new TypeToken<Map<String, List<Password>>>() {}.getType()
                            );

                            // Extract the user and their passwords
                            Map.Entry<String, List<Password>> entry = userPasswords.entrySet().iterator().next();
                            currentUser = entry.getKey();
                            passwordIterator = entry.getValue().iterator();
                        }
                        return passwordIterator != null && passwordIterator.hasNext();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                public Password next() {
                    if (passwordIterator == null || !passwordIterator.hasNext()) {
                        throw new NoSuchElementException("No more passwords available");
                    }
                    Password password = passwordIterator.next();
                    password.setUser(currentUser); // Set the user for the password
                    return password;
                }
            };

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

    public static Stream<User> loadUsers(String filePath) {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(filePath));

            jsonReader.beginArray();

            Iterable<User> iterable = () -> new Iterator<>() {
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
                public User next() {
                    try {
                        User user = gson.fromJson(jsonReader, User.class);
                        // Debug log to check the loaded User object
                        // System.out.println("Loaded User: " + user);
                        return user;
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing JSON", e);
                    }
                }
            };

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
    //     loadUsers("C:\\Users\\fangi\\Desktop\\AGH\\JPWP\\PassManager\\backend\\src\\main\\java\\JPWP\\backend\\database\\users.json")
    //         .map(p -> p.userName() + " " + p.password())
    //         .forEach(System.out::println);
    // }
}
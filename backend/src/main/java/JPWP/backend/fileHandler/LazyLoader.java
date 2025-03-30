package JPWP.backend.fileHandler;

import java.io.FileReader;
import java.io.Reader;
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
                private boolean insideUser = false;

                @Override
                public boolean hasNext() {
                    try {
                        // Check if there are more passwords in the current user's array or more users in the JSON array
                        if (insideUser) {
                            if(jsonReader.hasNext()) return true;
                            jsonReader.endArray(); // End the current user's passwords array
                            jsonReader.endObject(); // End the current user's object
                            insideUser = false; // Move to the next user
                            return jsonReader.hasNext(); // Check if there are more users in the JSON array
                        }

                        // If no more passwords in the current user's array, check for more users
                        if (!insideUser && jsonReader.hasNext()) {
                            return true; // More users in the JSON array
                        }

                        // If neither, return false (end of the JSON array)
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                public Password next() {
                    try {
                        if (!insideUser) {
                            // Start reading the next user object
                            if (jsonReader.hasNext()) {
                                jsonReader.beginObject();
                                currentUser = jsonReader.nextName(); // Read the user key
                                jsonReader.beginArray(); // Start reading the passwords array
                                insideUser = true;
                            }else{
                                return null; // No more users to read
                            }
                            
                        }

                        // Read the next password in the current user's array
                        if (jsonReader.hasNext()) {
                            Password password = gson.fromJson(jsonReader, Password.class);
                            password.setUser(currentUser); // Set the user for the password
                            return password;
                        } else {
                            // End the current user's passwords array and object
                            jsonReader.endArray();
                            jsonReader.endObject();
                            insideUser = false; // Move to the next user
                            return next(); // Recursively call next() to move to the next user
                        }
                    } catch (Exception e) {
                        throw new NoSuchElementException("Error reading next password: " + e.getMessage());
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
   
}
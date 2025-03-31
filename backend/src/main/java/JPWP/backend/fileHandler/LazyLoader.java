package JPWP.backend.fileHandler;

import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import JPWP.backend.Password;
import JPWP.backend.User;

public class LazyLoader {
    private static final Logger logger = LogManager.getLogger(LazyLoader.class);
    private static final Gson gson = new Gson();

    public static Stream<Password> loadPasswords(String filePath) {
        try {

            // logMemoryUsage("Before processing passwords");

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
                        // If neither, return false (end of the JSON array)

                        return !insideUser && jsonReader.hasNext();
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
                            // logMemoryUsage("Processing password for site: " + password.getSite().getNameSite());
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
            // logMemoryUsage("After processing passwords");
            return StreamSupport.stream(iterable.spliterator(), true)
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

    public static Iterator<User> loadUsers(String filePath) {
        try {
            // JsonReader jsonReader = new JsonReader(new FileReader(filePath));
            Reader reader = new FileReader(filePath);
            List<User> userMap = gson.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            Iterable<User> iterable = () -> new Iterator<>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < userMap.size();
                }

                @Override
                public User next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException("No more users to read.");
                    }
                    return userMap.get(index++);
                }
            };

            return iterable.iterator();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyIterator();
        }
    }
    private static void logMemoryUsage(String message) {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        logger.info("{} - Used Memory: {} MB", message, (usedMemory / 1024 / 1024));
    }
}
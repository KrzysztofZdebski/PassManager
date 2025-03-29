package JPWP.backend.fileHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import JPWP.backend.Password;
import JPWP.backend.Site;
import JPWP.backend.User;

public class DataWriter {
    private static final Gson gson = new GsonBuilder()
                                    .setExclusionStrategies(new SensitiveFieldExclusionStrategy())
                                    .create();

    public boolean updatePassword(String filePath, String siteName, String user, Password updatedPassword) {
        File inputFile = new File(filePath);
        File tempFile = new File(filePath + ".tmp");

        boolean updatedOut = false;

        try (JsonReader reader = new JsonReader(new FileReader(inputFile));
            JsonWriter writer = new JsonWriter(new FileWriter(tempFile))) {

            writer.setIndent("  "); // Set indentation for pretty printing
            reader.beginArray(); // Start reading the JSON array
            writer.beginArray(); // Start writing the updated JSON array

            while (reader.hasNext()) {
                reader.beginObject();
                writer.beginObject();
                while(reader.hasNext()){
                    String key = reader.nextName();
                    writer.name(key);
                    boolean corrUser = key.equals(user);
                    boolean updatedIn = false;

                    reader.beginArray();
                    writer.beginArray();
                    while(reader.hasNext()){
                        Password password = gson.fromJson(reader, Password.class);
                        if (corrUser && password.getSite().getNameSite().equals(siteName)) {
                            // Update the password for the given user and site
                            password.setEncyptedPassword(updatedPassword.getEncryptedPassword());
                            updatedOut = true; // Mark as updated
                            updatedIn = true;
                        }
                        gson.toJson(password, Password.class, writer); // Write the original password
                    }
                    if (!updatedIn && corrUser) {
                        gson.toJson(updatedPassword, Password.class, writer); // Write the updated password
                        updatedOut = true; // Mark as updated
                    }
                    writer.endArray();
                    reader.endArray();
                }
                writer.endObject();
                reader.endObject();
            }

            reader.endArray();

            // If the user doesn't exist, add a new entry
            if (!updatedOut) {
                Map<String, List<Password>> newUserEntry = new HashMap<>();
                newUserEntry.put(user, Collections.singletonList(updatedPassword));
                gson.toJson(newUserEntry, new TypeToken<Map<String, List<Password>>>() {}.getType(), writer);
                updatedOut = true; // Mark as updated
            }

            writer.endArray();
            reader.close();
            writer.close();

            // Replace the original file with the updated file
            Files.move(tempFile.toPath(), inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return updatedOut;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removePassword(String filePath, String siteName, String user) {
        File inputFile = new File(filePath);
        File tempFile = new File(filePath + ".tmp");

        boolean updatedOut = false;

        try (JsonReader reader = new JsonReader(new FileReader(inputFile));
            JsonWriter writer = new JsonWriter(new FileWriter(tempFile))) {

            writer.setIndent("  "); // Set indentation for pretty printing
            reader.beginArray(); // Start reading the JSON array
            writer.beginArray(); // Start writing the updated JSON array

            while (reader.hasNext()) {
                reader.beginObject();
                writer.beginObject();
                while(reader.hasNext()){
                    String key = reader.nextName();
                    writer.name(key);
                    boolean corrUser = key.equals(user);

                    reader.beginArray();
                    writer.beginArray();
                    while(reader.hasNext()){
                        Password password = gson.fromJson(reader, Password.class);
                        if (corrUser && password.getSite().getNameSite().equals(siteName)) {
                            // Remove the password for the given user and site
                            updatedOut = true;
                        } else {
                            // Write the original password if it doesn't match the criteria
                            gson.toJson(password, Password.class, writer);
                        }
                    }
                    writer.endArray();
                    reader.endArray();
                }
                writer.endObject();
                reader.endObject();
            }

            reader.endArray();
            writer.endArray();
            reader.close();
            writer.close();

            // Replace the original file with the updated file
            Files.move(tempFile.toPath(), inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return updatedOut;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addUser(String filePath, String userName, String password) {
        File inputFile = new File(filePath);
        File tempFile = new File(filePath + ".tmp");
        User newUser = new User(userName, password);

        try (JsonReader reader = new JsonReader(new FileReader(inputFile));
             JsonWriter writer = new JsonWriter(new FileWriter(tempFile))) {

            reader.beginArray(); // Start reading the JSON array
            writer.beginArray(); // Start writing the updated JSON array

            boolean userExists = false;

            while (reader.hasNext() && !userExists) {
                User user = gson.fromJson(reader, User.class);

                // Check if this is the entry to update
                if (user.userName().equals(userName)) {
                    userExists = true; // User already exists
                } else {
                    gson.toJson(user, User.class, writer); // Write the original user
                }
            }

            if (userExists) {
                // System.out.println("User already exists, not adding a new entry.");
                writer.endArray();
                return false; // Stop writing and return immediately
            }

            gson.toJson(newUser, User.class, writer); // Write the new user

            reader.endArray();
            writer.endArray();
            reader.close();
            writer.close();

            // Replace the original file with the updated file
            Files.move(tempFile.toPath(), inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            // System.out.println("File replaced successfully.");

            return true; // Return true if a new user was added

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Ensure the temporary file is deleted if it exists
            if (tempFile.exists()) {
                if (!tempFile.delete()) {
                    System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
                }
            }
        }
    }
}
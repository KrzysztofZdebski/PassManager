package JPWP.backend.fileHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import JPWP.backend.Password;

public class DataWriter {
    private static final Gson gson = new GsonBuilder()
                                    .setExclusionStrategies(new SensitiveFieldExclusionStrategy())
                                    .create();

    public static boolean updatePassword(String filePath, String siteName, String user, Password updatedPassword) {
        File inputFile = new File(filePath);
        File tempFile = new File(filePath + ".tmp");

        try (JsonReader reader = new JsonReader(new FileReader(inputFile));
             JsonWriter writer = new JsonWriter(new FileWriter(tempFile))) {

            reader.beginArray(); // Start reading the JSON array
            writer.beginArray(); // Start writing the updated JSON array

            boolean updated = false;

            while (reader.hasNext()) {
                Password password = gson.fromJson(reader, Password.class);

                // Check if this is the entry to update
                if (password.getSite().getNameSite().equals(siteName) && password.getUser().equals(user)) {
                    gson.toJson(updatedPassword, Password.class, writer); // Write the updated password
                    updated = true;
                } else {
                    gson.toJson(password, Password.class, writer); // Write the original password
                }
            }
            if(!updated){
                gson.toJson(updatedPassword, Password.class, writer);
            }

            reader.endArray();
            reader.close();
            writer.endArray();
            writer.close();
            Thread.sleep(1000);
            // Replace the original file with the updated file
            if (!tempFile.exists()) {
                throw new IOException("Temporary file does not exist: " + tempFile.getAbsolutePath());
            }

            try {
                Files.move(tempFile.toPath(), inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File replaced successfully.");
            } catch (IOException e) {
                System.err.println("Failed to replace the original file: " + e.getMessage());
                throw e;
            }

            return updated;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removePassword(String filePath, String siteName, String user) {
        File inputFile = new File(filePath);
        File tempFile = new File(filePath + ".tmp");

        try (JsonReader reader = new JsonReader(new FileReader(inputFile));
             JsonWriter writer = new JsonWriter(new FileWriter(tempFile))) {

            reader.beginArray(); // Start reading the JSON array
            writer.beginArray(); // Start writing the updated JSON array

            boolean removed = false;

            while (reader.hasNext()) {
                Password password = gson.fromJson(reader, Password.class);

                // Check if this is the entry to remove
                if (password.getSite().getNameSite().equals(siteName) && password.getUser().equals(user)) {
                    removed = true; // Skip writing this password to the new file
                } else {
                    gson.toJson(password, Password.class, writer); // Write the original password
                }
            }

            reader.endArray();
            reader.close();
            writer.endArray();
            writer.close();

            // Replace the original file with the updated file
            try {
                Files.move(tempFile.toPath(), inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File replaced successfully.");
            } catch (IOException e) {
                System.err.println("Failed to replace the original file: " + e.getMessage());
                throw e;
            }

            return removed;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
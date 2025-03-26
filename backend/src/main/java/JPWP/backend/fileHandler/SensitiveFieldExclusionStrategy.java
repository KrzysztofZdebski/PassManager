package JPWP.backend.fileHandler;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class SensitiveFieldExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        // Exclude fields by name
        return f.getName().equals("SECRET_KEY") || f.getName().equals("SALT") || f.getName().equals("key");
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false; // Do not exclude any class
    }
}
package JPWP.backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Stream;

import com.google.gson.Gson;


public class LazyLoader {
    private static final Gson gson = new Gson();

    @SuppressWarnings("resource")
    public static Stream<Password> loadPasswords(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            return reader.lines()
                    .map(line -> gson.fromJson(line, Password.class))
                    .onClose(() -> {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

    public static void main(String[] args) {
        loadPasswords("C:\\Users\\fangi\\Desktop\\AGH\\JPWP\\PassManager\\backend\\src\\main\\java\\JPWP\\backend\\database\\passwords.json").forEach(System.out::println);
    }
}
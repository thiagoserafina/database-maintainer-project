package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Logger {

    private static final String logPath = "log.txt";

    public static void log(String message) {
        try (FileWriter fw = new FileWriter(logPath, true)) {
            fw.write(LocalDateTime.now() + " - " + message + "\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

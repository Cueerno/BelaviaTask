package com.radiuk.belavia_task.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class FileUtil {

    public static long getNumberOfLinesInFile(Path file) throws IOException {
        try (Stream<String> s = Files.lines(file, StandardCharsets.UTF_8)) {
            return s.count();
        }
    }

    public static void deleteDirectoryWithSubdirectoriesAndFiles(Path dir) throws IOException {
        if (Files.notExists(dir)) {
            return;
        }

        try (var paths = Files.walk(dir)) {
            paths
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException exception) {
                            throw new RuntimeException("Failed to delete: " + path, exception);
                        }
                    });
        }
    }
}

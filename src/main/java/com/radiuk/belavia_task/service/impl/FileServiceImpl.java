package com.radiuk.belavia_task.service.impl;

import com.radiuk.belavia_task.config.RandomValuesConfig;
import com.radiuk.belavia_task.exception.FileCombiningException;
import com.radiuk.belavia_task.exception.FileProcessingException;
import com.radiuk.belavia_task.service.FileService;
import lombok.Builder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static com.radiuk.belavia_task.util.FileUtil.deleteDirectoryWithSubdirectoriesAndFiles;
import static com.radiuk.belavia_task.util.Randomizer.*;

@Builder
public class FileServiceImpl implements FileService {

    RandomValuesConfig randomValuesConfig;

    private int progressInterval;
    private int filesCount;
    private int linesPerFile;
    private String fieldSeparator;
    private String filesNamePrefix;
    private String combinedFileName;

    @Override
    public void createFileWithRandomLines(Path directory) {
        try {
            deleteDirectoryWithSubdirectoriesAndFiles(directory);
            Files.createDirectories(directory);

            System.out.println("Creating " + filesCount + " files...");

            for (int filesCreatedCount = 1; filesCreatedCount <= filesCount; filesCreatedCount++) {
                Path file = directory.resolve(filesNamePrefix + filesCreatedCount + ".txt");

                try {
                    Files.createFile(file);
                } catch (IOException exception) {
                    throw new FileProcessingException("Failed to create file: " + file, exception);
                }

                writeRandomDataToFile(file, linesPerFile);

                if (filesCreatedCount % progressInterval == 0) {
                    System.out.println(filesCreatedCount + " files created");
                }
            }
        } catch (IOException exception) {
            throw new FileProcessingException("Cannot create directory: " + directory, exception);
        }
    }

    @Override
    public void combineFilesFromInitialDirectory(Path initialDirectory, Path directoryWithCombinedFile, String substringToRemove) {
        Path combinedFile = prepareCombinedFile(directoryWithCombinedFile);
        int removedCount = 0;

        try (BufferedWriter writer = Files.newBufferedWriter(combinedFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             DirectoryStream<Path> files = Files.newDirectoryStream(initialDirectory)) {

            System.out.println("Combining " + filesCount + " files...");

            for (Path file : files) {
                if (!Files.isRegularFile(file)) continue;
                removedCount = processSingleFile(file, substringToRemove, removedCount, writer);
            }

        } catch (IOException exception) {
            throw new FileCombiningException("Error combining files in directory: " + initialDirectory);
        }

        System.out.println("All files combined in " + combinedFile);
        System.out.println("Removed lines: " + removedCount);
    }

    @Override
    public void removeLinesFromFileContaining(Path file, String substringToRemove) {
        System.out.println("Remove lines with substring: [" + substringToRemove + "] ...");
        long removedCount = 0;

        try {
            Path temp = Files.createTempFile("temp-" + file.getFileName().toString(), ".txt");

            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
                 BufferedWriter writer = Files.newBufferedWriter(temp, StandardOpenOption.TRUNCATE_EXISTING)
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (shouldRemoveLine(line, substringToRemove)) {
                        removedCount++;
                        continue;
                    }

                    writer.write(line);
                    writer.newLine();
                }
            }

            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException exception) {
            throw new FileProcessingException("Error removing lines from file '" + file + "': " + exception.getMessage(), exception);
        }

        System.out.println("Removed " + removedCount);
    }

    private Path prepareCombinedFile(Path directoryWithCombinedFile) {
        try {
            deleteDirectoryWithSubdirectoriesAndFiles(directoryWithCombinedFile);
            Files.createDirectories(directoryWithCombinedFile);
            Path combinedFile = directoryWithCombinedFile.resolve(combinedFileName);
            Files.createFile(combinedFile);
            return combinedFile;
        } catch (IOException exception) {
            throw new FileProcessingException("Cannot prepare combined file in: " + directoryWithCombinedFile, exception);
        }
    }

    private int processSingleFile(Path file, String substringToRemove, int removedCount, BufferedWriter writer) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (substringToRemove != null && !substringToRemove.isEmpty() && line.contains(substringToRemove)) {
                    removedCount++;
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new FileProcessingException("Error reading file: " + file, exception);
        }

        return removedCount;
    }

    private void writeRandomDataToFile(Path file, int linesPerFile) {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (int i = 0; i < linesPerFile; i++) {
                writer.write(getRandomDataForFile());
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new FileProcessingException("Failed to write random data to file: " + file, exception);
        }
    }

    private boolean shouldRemoveLine(String line, String substringToRemove) {
        return substringToRemove != null && !substringToRemove.isEmpty() && line.contains(substringToRemove);
    }

    private String getRandomDataForFile() {
        return getRandomDate(randomValuesConfig.getDateYearsBack()).format(FORMATTER) + fieldSeparator +
                getRandomString(LATIN_LETTERS, randomValuesConfig.getLatinLength()) + fieldSeparator +
                getRandomString(CYRILLIC_LETTERS, randomValuesConfig.getRussianLength()) + fieldSeparator +
                getRandomEvenInt(randomValuesConfig.getEvenIntMin(), randomValuesConfig.getEvenIntMax()) + fieldSeparator +
                getRandomDecimal(randomValuesConfig.getDecimalMin(), randomValuesConfig.getDecimalMax(), randomValuesConfig.getDecimalScale());
    }
}

package com.radiuk.belavia_task;

import com.radiuk.belavia_task.exception.*;
import com.radiuk.belavia_task.service.*;
import lombok.Builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

@Builder
public class TextDataEtlCli {

    private String initialDirectoryPath;
    private String directoryPathWithCombinedFile;
    private String combinedFileName;

    private FileService fileService;
    private DbImportService dbImportService;

    private final Scanner scanner = new Scanner(System.in);

    public void runApp() throws IOException {
        Path initialDirectory = Paths.get(initialDirectoryPath);
        Path directoryWithCombinedFile = Paths.get(directoryPathWithCombinedFile);
        Path combinedFile = directoryWithCombinedFile.resolve(combinedFileName);

        askToGenerateFiles(initialDirectory);

        while (true) {
            try {
                printMenu();
                String choice = readLine("Make choice: ");

                switch (choice) {
                    case "1" -> {
                        try {
                            combineFilesWithOptionalSubstringRemoval(initialDirectory, directoryWithCombinedFile);
                        } catch (FileProcessingException exception) {
                            System.err.println("Error combining files: " + exception.getMessage());
                        }
                    }
                    case "2" -> {
                        try {
                            removeLinesContainingSubstringFromCombinedFile(combinedFile);
                        } catch (FileProcessingException exception) {
                            System.err.println("Error removing lines: " + exception.getMessage());
                        }
                    }
                    case "3" -> {
                        try {
                            importCombinedFileToDatabase(combinedFile);
                        } catch (DatabaseImportException exception) {
                            System.err.println("Database import: " + exception.getMessage());
                        }
                    }
                    case "4" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.\n");
                }
            } catch (Exception exception) {
                System.err.println("Unexpected error: " + exception);
            }
        }
    }

    private void askToGenerateFiles(Path initialDirectory) throws IOException {
        if (Files.exists(initialDirectory)) {
            String answer = readLine("Generate new files? (Previous files will be deleted) (y/n): ").trim().toLowerCase();

            if (answer.equals("y")) {
                fileService.createFileWithRandomLines(initialDirectory);
                System.out.println("Files generated.\n");
            }
        } else {
            fileService.createFileWithRandomLines(initialDirectory);
            System.out.println("Files generated.\n");
        }
    }

    private void combineFilesWithOptionalSubstringRemoval(Path initialDirectory, Path directoryWithCombinedFile) throws IOException {
        String substringToRemove = readLine("Input substring to remove: ").trim();
        fileService.combineFilesFromInitialDirectory(initialDirectory, directoryWithCombinedFile, substringToRemove);
    }

    private void removeLinesContainingSubstringFromCombinedFile(Path combinedFile) throws IOException {
        String substringToRemove = readLine("Input substring to remove: ").trim();
        fileService.removeLinesFromFileContaining(combinedFile, substringToRemove);
    }

    private void importCombinedFileToDatabase(Path combinedFile) throws IOException {
        dbImportService.importCombinedFileToDb(combinedFile);
    }

    private String readLine(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private void printMenu() {
        System.out.println("""
                ┌───────────────────────────────┐
                │            MENU               │
                ├───────────────────────────────┤
                │ 1. Combine files              │
                │ 2. Substring processing       │
                │ 3. Import to database         │
                │ 4. Exit                       │
                └───────────────────────────────┘
                """);
    }
}

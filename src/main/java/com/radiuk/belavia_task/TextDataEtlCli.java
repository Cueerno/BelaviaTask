package com.radiuk.belavia_task;

import com.radiuk.belavia_task.exception.DatabaseImportException;
import com.radiuk.belavia_task.exception.FileProcessingException;
import com.radiuk.belavia_task.service.DbImportService;
import com.radiuk.belavia_task.service.FileService;
import lombok.Builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

@Builder
public class TextDataEtlCli {

    private static final String CONFIRMING_CREATION_OF_NEW_FILES = "Y";

    private String initialDirectoryPath;
    private String directoryPathWithCombinedFile;
    private String combinedFileName;

    private FileService fileService;
    private DbImportService dbImportService;

    private final Scanner scanner = new Scanner(System.in);

    public void runApp() throws FileProcessingException {
        Path initialDirectory = Paths.get(initialDirectoryPath);
        Path directoryWithCombinedFile = Paths.get(directoryPathWithCombinedFile);
        Path combinedFile = directoryWithCombinedFile.resolve(combinedFileName);

        try {
            askToGenerateFiles(initialDirectory);
        } catch (IOException exception) {
            printException("File processing error: ", exception);
        }


        while (true) {
            printMenu();
            String choice = readLine("Make choice: ");

            try {
                switch (choice) {
                    case "1" -> combineFilesWithOptionalSubstringRemoval(initialDirectory, directoryWithCombinedFile);

                    case "2" -> removeLinesContainingSubstringFromCombinedFile(combinedFile);
                    case "3" -> importCombinedFileToDatabase(combinedFile);
                    case "4" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.\n");
                }
            } catch (FileProcessingException exception) {
                printException("File processing error: ", exception);
            } catch (DatabaseImportException exception) {
                printException("Database import error: ", exception);
            } catch (Exception exception) {
                printException("Unexpected exception: ", exception);
            }
        }
    }

    private void askToGenerateFiles(Path initialDirectory) throws IOException, FileProcessingException {
        if (Files.exists(initialDirectory)) {
            String answer = readLine("Generate new files? (Previous files will be deleted) (y/n): ").trim().toLowerCase();

            if (answer.equalsIgnoreCase(CONFIRMING_CREATION_OF_NEW_FILES)) {
                fileService.createFileWithRandomLines(initialDirectory);
                System.out.println("Files generated.\n");
            }
        } else {
            fileService.createFileWithRandomLines(initialDirectory);
            System.out.println("Files generated.\n");
        }
    }

    private void combineFilesWithOptionalSubstringRemoval(Path initialDirectory, Path directoryWithCombinedFile) throws IOException, FileProcessingException {
        String substringToRemove = readLine("Input substring to remove (empty line - don't remove anything): ").trim();
        fileService.combineFilesFromInitialDirectory(initialDirectory, directoryWithCombinedFile, substringToRemove);
    }

    private void removeLinesContainingSubstringFromCombinedFile(Path combinedFile) throws IOException, FileProcessingException {
        String substringToRemove = readLine("Input substring to remove: ").trim();
        fileService.removeLinesFromFileContaining(combinedFile, substringToRemove);
    }

    private void importCombinedFileToDatabase(Path combinedFile) throws IOException, DatabaseImportException {
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

    private void printException(String message, Exception exception) {
        System.err.println("\n┌─ ERROR ──────────────────────────────┐");
        System.err.println("│ " + message);
        System.err.println("│ " + exception.getMessage());
        System.err.println("└──────────────────────────────────────┘\n");
    }
}

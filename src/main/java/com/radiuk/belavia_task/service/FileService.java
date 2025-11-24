package com.radiuk.belavia_task.service;

import com.radiuk.belavia_task.exception.FileProcessingException;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {

    void createFileWithRandomLines(Path directory) throws IOException, FileProcessingException;

    void combineFilesFromInitialDirectory(Path initialDirectory, Path directoryWithCombinedFile, String substringToRemove) throws IOException, FileProcessingException;

    void removeLinesFromFileContaining(Path file, String substringToRemove) throws IOException, FileProcessingException;
}

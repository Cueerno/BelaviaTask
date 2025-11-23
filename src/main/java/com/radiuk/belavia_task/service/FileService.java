package com.radiuk.belavia_task.service;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {

    void createFileWithRandomLines(Path directory) throws IOException;

    void combineFilesFromInitialDirectory(Path initialDirectory, Path directoryWithCombinedFile, String substringToRemove) throws IOException;

    void removeLinesFromFileContaining(Path file, String substringToRemove) throws IOException;
}

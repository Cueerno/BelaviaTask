package com.radiuk.belavia_task.service;

import com.radiuk.belavia_task.exception.DatabaseImportException;

import java.io.IOException;
import java.nio.file.Path;

public interface DbImportService {

    void importCombinedFileToDb(Path file) throws IOException, DatabaseImportException;
}

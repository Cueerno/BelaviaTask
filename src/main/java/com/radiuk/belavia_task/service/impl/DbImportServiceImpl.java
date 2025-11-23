 package com.radiuk.belavia_task.service.impl;

import com.radiuk.belavia_task.exception.DatabaseImportException;
import com.radiuk.belavia_task.service.DbImportService;
import com.radiuk.belavia_task.util.FileUtil;
import lombok.Builder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.radiuk.belavia_task.util.Randomizer.FORMATTER;

@Builder
public class DbImportServiceImpl implements DbImportService {

    private final int batchSize;
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    private static final String INSERT_INTO_RANDOM_RECORDS_RANDOM_DATA = """
            INSERT INTO public.random_records (date, latin, cyrillic, even_int, decimal_value)
            VALUES (?, ?, ?, ?, ?)
            """;

    public void importCombinedFileToDb(Path combinedFile) {
        transactionTemplate.execute(status -> {
            try {
                doImport(combinedFile);
            } catch (IOException exception) {
                status.setRollbackOnly();
                throw new DatabaseImportException("Failed to import file to database: " + combinedFile, exception);
            }
            return null;
        });
    }

    private void doImport(Path combinedFile) throws IOException {
        long totalLines = FileUtil.getNumberOfLinesInFile(combinedFile);
        List<Object[]> batchArgs = readFileToBatch(combinedFile, batchSize);
        executeBatchInsertWithProgress(batchArgs, batchSize, totalLines);
    }

    private List<Object[]> readFileToBatch(Path file, int batchSize) throws IOException {
        List<Object[]> batchArgs = new ArrayList<>(batchSize);
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|");
                batchArgs.add(parseDataFromLine(parts));
            }
        }
        return batchArgs;
    }

    private void executeBatchInsertWithProgress(List<Object[]> batchArgs, int batchSize, long totalLines) {
        int totalImported = 0;

        for (int i = 0; i < batchArgs.size(); i += batchSize) {
            int end = Math.min(i + batchSize, batchArgs.size());
            List<Object[]> batch = batchArgs.subList(i, end);

            jdbcTemplate.batchUpdate(INSERT_INTO_RANDOM_RECORDS_RANDOM_DATA, batch);
            totalImported += batch.size();

            System.out.println("Imported: " + totalImported + ". Remaining: " + (totalLines - totalImported));
        }

        System.out.println("The file import is complete. Total lines: " + totalImported);
    }

    private Object[] parseDataFromLine(String[] parts) {
        return new Object[]{
                LocalDate.parse(parts[0], FORMATTER),
                parts[1],
                parts[2],
                Integer.parseInt(parts[3]),
                new BigDecimal(parts[4])
        };
    }
}

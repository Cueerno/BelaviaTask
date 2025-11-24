 package com.radiuk.belavia_task.service.impl;

import com.radiuk.belavia_task.exception.DatabaseImportException;
import com.radiuk.belavia_task.model.RandomRecord;
import com.radiuk.belavia_task.service.DbImportService;
import com.radiuk.belavia_task.util.FileUtil;
import lombok.Builder;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
            } catch (NumberFormatException | IOException exception) {
                status.setRollbackOnly();
                throw new DatabaseImportException("Failed to import file to database: " + combinedFile, exception);
            }
            return null;
        });
    }

    private void doImport(Path combinedFile) throws IOException {
        long totalLines = FileUtil.getNumberOfLinesInFile(combinedFile);
        List<RandomRecord> batchArgs = readFileToBatch(combinedFile, batchSize);
        executeBatchInsertWithProgress(batchArgs, batchSize, totalLines);
    }

    private List<RandomRecord> readFileToBatch(Path file, int batchSize) throws IOException {
        List<RandomRecord> batchArgs = new ArrayList<>(batchSize);
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|");
                batchArgs.add(parseDataFromLine(parts));
            }
        }
        return batchArgs;
    }

    private void executeBatchInsertWithProgress(List<RandomRecord> batchArgs, int batchSize, long totalLines) {
        int totalImported = 0;

        for (int i = 0; i < batchArgs.size(); i += batchSize) {
            int end = Math.min(i + batchSize, batchArgs.size());
            List<RandomRecord> batch = batchArgs.subList(i, end);

            jdbcTemplate.batchUpdate(INSERT_INTO_RANDOM_RECORDS_RANDOM_DATA, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    RandomRecord record = batch.get(i);

                    ps.setString(1, record.getLatin());
                    ps.setString(2, record.getCyrillic());
                    ps.setInt(3, record.getEvenInt());
                    ps.setBigDecimal(4, record.getDecimal());
                }

                @Override
                public int getBatchSize() {
                    return batch.size();
                }
            });

            totalImported += batch.size();

            System.out.println("Imported: " + totalImported + ". Remaining: " + (totalLines - totalImported));
        }

        System.out.println("The file import is complete. Total lines: " + totalImported);
    }

    private RandomRecord parseDataFromLine(String[] parts) {
        try {
            return RandomRecord.builder()
                    .date(LocalDate.parse(parts[0], FORMATTER))
                    .latin(parts[1])
                    .cyrillic(parts[2])
                    .evenInt(Integer.parseInt(parts[3]))
                    .decimal(new BigDecimal(parts[4]))
                    .build();
        } catch (NumberFormatException exception) {
            throw new DatabaseImportException("Failed to parse data: " + Arrays.toString(parts), exception);
        }
    }
}

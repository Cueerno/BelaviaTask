package com.radiuk.belavia_task.config;

import com.radiuk.belavia_task.TextDataEtlCli;
import com.radiuk.belavia_task.service.DbImportService;
import com.radiuk.belavia_task.service.FileService;
import com.radiuk.belavia_task.service.impl.DbImportServiceImpl;
import com.radiuk.belavia_task.service.impl.FileServiceImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

public class TextDataEtlCliConfig {

    public static TextDataEtlCli buildTextDataEtlCli() {
        PropertyConfig propertyConfig = new PropertyConfig("config.properties");

        DataSource dataSource = DataSourceConfig.createDataSource(
                propertyConfig.getString("datasource.url"),
                propertyConfig.getString("datasource.username"),
                propertyConfig.getString("datasource.password")
        );

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        String combinedFileName = propertyConfig.getString("files.combined-name");

        FileService fileService = FileServiceImpl.builder()
                .randomValuesConfig(new RandomValuesConfig(propertyConfig))
                .combinedFileName(combinedFileName)
                .filesCount(propertyConfig.getInt("files.count"))
                .fieldSeparator(propertyConfig.getString("files.field-separator"))
                .filesNamePrefix(propertyConfig.getString("files.generated-prefix"))
                .linesPerFile(propertyConfig.getInt("files.lines-per-file"))
                .progressInterval(propertyConfig.getInt("files.progress-interval"))
                .build();

        DbImportService dbImportService = DbImportServiceImpl.builder()
                .batchSize(propertyConfig.getInt("db.batch-size"))
                .jdbcTemplate(jdbcTemplate)
                .transactionTemplate(new TransactionTemplate(new DataSourceTransactionManager(dataSource)))
                .build();

        return TextDataEtlCli.builder()
                .combinedFileName(combinedFileName)
                .initialDirectoryPath(propertyConfig.getString("files.initial-directory"))
                .dbImportService(dbImportService)
                .fileService(fileService)
                .directoryPathWithCombinedFile(propertyConfig.getString("files.directory-with-combined-file"))
                .build();
    }
}

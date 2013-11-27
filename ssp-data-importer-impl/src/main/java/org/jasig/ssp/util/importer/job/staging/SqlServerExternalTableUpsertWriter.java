package org.jasig.ssp.util.importer.job.staging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlServerExternalTableUpsertWriter implements ItemWriter<RawItem>,
        StepExecutionListener {

    private Resource currentResource;
    private String[] orderedHeaders = null;
    private MetadataConfigurations metadataRepository;
    private StepExecution stepExecution;
 
    @Autowired
    private DataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(SqlServerExternalTableUpsertWriter.class);

    
    @Override
    public void write(List<? extends RawItem> items) throws Exception {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<String> batchedStatements = new ArrayList<String>();

        String fileName = items.get(0).getResource().getFilename();
        String[] tableName = fileName.split("\\.");

        Object batchStart = stepExecution.getExecutionContext().get(
                "batchStart");
        Object batchStop = stepExecution.getExecutionContext().get("batchStop");

        RawItem item = items.get(0);
        if (currentResource == null) {
            this.orderedHeaders = writeHeader(items.get(0));
            this.currentResource = items.get(0).getResource();
        }
        Resource itemResource = item.getResource();
        if (!(this.currentResource.equals(itemResource))) {
            say();
            this.orderedHeaders = writeHeader(item);
            this.currentResource = itemResource;
        }
        StringBuilder insertSql = new StringBuilder();
        insertSql.append(" MERGE INTO " + tableName[0]
                + " as target USING stg_" + tableName[0] + " as source ON ");

        List<String> tableKeys = metadataRepository.getRepository()
                .getColumnMetadataRepository()
                .getTableMetadata(new TableReference(tableName[0]))
                .getTableKeys();

        // There are a few external tables that don't (yet) have natural keys,
        // in these cases we've enforced the key on the staging table
        // so in cases where the external table does not have any keys, we look
        // towards the corresponding staging table for them
        if (tableKeys.isEmpty()) {
            tableKeys = metadataRepository
                    .getRepository()
                    .getColumnMetadataRepository()
                    .getTableMetadata(new TableReference("stg_" + tableName[0]))
                    .getTableKeys();
        }
        for (String key : tableKeys) {
            insertSql.append("target." + key + " = source." + key + ",");
        }
        insertSql.setLength(insertSql.length() - 1); // trim comma

        insertSql.append(" WHEN NOT MATCHED AND source.batch_id >= "
                + batchStart + " and source.batch_id <= " + batchStop
                + " THEN INSERT (");

        StringBuilder valuesSqlBuilder = new StringBuilder();
        valuesSqlBuilder.append(" VALUES ( ");
        for (String header : this.orderedHeaders) {

            insertSql.append(header).append(",");
            valuesSqlBuilder.append("source." + header).append(",");
        }
        insertSql.setLength(insertSql.length() - 1); // trim comma
        insertSql.append(")");
        valuesSqlBuilder.setLength(valuesSqlBuilder.length() - 1); // trim comma
        insertSql.append(valuesSqlBuilder);
        insertSql.append(")");
        insertSql.append(" WHEN MATCHED AND source.batch_id >= " + batchStart
                + " and source.batch_id <= " + batchStop + " THEN UPDATE SET ");

        for (String header : this.orderedHeaders) {
            if (tableKeys.indexOf(header) < 0) {
                insertSql.append("target." + header + "=source." + header)
                        .append(",");
            }
        }
        insertSql.setLength(insertSql.length() - 1); // trim comma
        insertSql.append(";");

        batchedStatements.add(insertSql.toString());
        say(insertSql);
        int[] results = jdbcTemplate.batchUpdate(batchedStatements.toArray(new String[]{}));
        Integer numInsertedUpdated = (Integer) stepExecution.getExecutionContext().get(
                "numInsertedUpdated");
        numInsertedUpdated = numInsertedUpdated == null ? 0 : numInsertedUpdated;
        numInsertedUpdated += results[0];
        stepExecution.getExecutionContext().put("numInsertedUpdated", numInsertedUpdated);
        
        say("******UPSERT******" + " batch start:" + batchStart + " batchstop:"
                + batchStop);
    }

    private String[] writeHeader(RawItem item) {
        Map<String, String> firstRecord = item.getRecord();
        StringBuilder sb = new StringBuilder();
        List<String> headerColumns = new ArrayList<String>();
        for (String key : firstRecord.keySet()) {
            sb.append(key).append(",");
            headerColumns.add(key);
        }
        sb.setLength(sb.length() - 1); // trim comma
        return headerColumns.toArray(new String[headerColumns.size()]);
    }

    private void say(Object message) {
        logger.info(message.toString());
    }

    private void say() {
        say("");
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public MetadataConfigurations getMetadataRepository() {
        return metadataRepository;
    }

    public void setMetadataRepository(MetadataConfigurations metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public StepExecution getStepExecution() {
        return stepExecution;
    }

    public void setStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution arg0) {
        return ExitStatus.COMPLETED;
    }

    @Override
    public void beforeStep(StepExecution arg0) {
        this.stepExecution = arg0;
    }

}

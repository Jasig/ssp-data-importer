/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.jasig.ssp.util.importer.job.staging.StagingConstants.STAGING_TABLE_BATCH_ID_COLUMN;
import static org.jasig.ssp.util.importer.job.staging.StagingConstants.STAGING_TABLE_PREFIX;

public class SqlServerExternalTableUpsertWriter implements ItemWriter<RawItem>,
        StepExecutionListener {

    private Resource currentResource;
    private String[] orderedHeaders = null;
    private MetadataConfigurations metadataRepository;
    private StepExecution stepExecution;

    @Autowired
    private DataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(SqlServerExternalTableUpsertWriter.class);
    private static final Logger queryLogger = LoggerFactory.getLogger("QUERYLOG." + SqlServerExternalTableUpsertWriter.class);


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
            this.orderedHeaders = writeHeader(item);
            this.currentResource = itemResource;
        }
        StringBuilder insertSql = new StringBuilder();
        insertSql.append(" MERGE INTO " + tableName[0]
                + " as target USING " + STAGING_TABLE_PREFIX + tableName[0] + " as source ON (");

        List<String> tableKeys = metadataRepository
                    .getRepository()
                    .getColumnMetadataRepository()
                    .getTableMetadata(new TableReference(STAGING_TABLE_PREFIX + tableName[0]))
                    .getTableKeys();

        for (String key : tableKeys) {
            insertSql.append(" (target." + key + " = source." + key + " or target." + key + " is null and source." + key + " is null) and ");
        }
        insertSql.setLength(insertSql.length() - 4); // trim 'and '

        insertSql.append(") WHEN NOT MATCHED AND source." + STAGING_TABLE_BATCH_ID_COLUMN + " >= "
                + batchStart + " and source." + STAGING_TABLE_BATCH_ID_COLUMN + " <= " + batchStop
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
        insertSql.append(" WHEN MATCHED AND source." + STAGING_TABLE_BATCH_ID_COLUMN + " >= " + batchStart
                + " and source." + STAGING_TABLE_BATCH_ID_COLUMN + " <= " + batchStop + " THEN UPDATE SET ");

        for (String header : this.orderedHeaders) {
            // We don't skip key columns b/c some tables are entirely keys.
            // so a bit wasteful, but makes statement building logic a bit
            // simpler than figuring out if we can leave the update
            // clause off altogether
            insertSql.append("target.").append(header).append("=source.").append(header).append(",");
        }

        insertSql.setLength(insertSql.length() - 1); // trim comma
        insertSql.append(";");

        batchedStatements.add(insertSql.toString());
        sayQuery(insertSql);
        try {
            int[] results = jdbcTemplate.batchUpdate(batchedStatements.toArray(new String[]{}));
            Integer numInsertedUpdated = (Integer) stepExecution.getExecutionContext().get(
                    "numInsertedUpdated");
            numInsertedUpdated = numInsertedUpdated == null ? 0 : numInsertedUpdated;
            if ( results.length >= 1 ) {
                numInsertedUpdated = numInsertedUpdated + results[0];
            }
            if ( results.length >= 2 ) {
                numInsertedUpdated = numInsertedUpdated + results[1];
            }
            stepExecution.getExecutionContext().put("numInsertedUpdated", numInsertedUpdated);

            say("******UPSERT******" + " batch start:" + batchStart + " batchstop:"
                    + batchStop);
        } catch(Exception e) {
            throw new NotSkippableException(e);
        }
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

    private void sayQuery(Object message) {
        queryLogger.info(message.toString());
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

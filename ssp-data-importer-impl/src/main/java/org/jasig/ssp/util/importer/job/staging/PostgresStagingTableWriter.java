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

import org.jarbframework.utils.orm.ColumnReference;
import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PostgresStagingTableWriter implements ItemWriter<RawItem>,
        StepExecutionListener {

    private Resource currentResource;
    private String[] orderedHeaders = null;
    private MetadataConfigurations metadataRepository;
    private StepExecution stepExecution;

    private static final Logger logger = LoggerFactory.getLogger(PostgresStagingTableWriter.class);
    private static final Logger queryLogger = LoggerFactory.getLogger("QUERYLOG." + PostgresStagingTableWriter.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void write(final List<? extends RawItem> items)  {

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String fileName = items.get(0).getResource().getFilename();
        final String[] tableName = fileName.split("\\.");

        Integer batchStart = (Integer) (stepExecution.getExecutionContext()
                .get("batchStart") == null ? null : stepExecution
                .getExecutionContext().get("batchStart"));
        Integer batchStop = (Integer) (stepExecution.getExecutionContext().get(
                "batchStop") == null ? null : stepExecution
                .getExecutionContext().get("batchStop"));
        Object currentEntity = stepExecution.getExecutionContext().get(
                "currentEntity");

        if (currentEntity == null || !currentEntity.equals(tableName[0])) {
            batchStart = 0;
            batchStop = items.size() - 1;
            currentEntity = tableName[0];
            stepExecution.getExecutionContext().put("currentEntity",
                    currentEntity);
            stepExecution.getExecutionContext().put("batchStart", batchStart);
            stepExecution.getExecutionContext().put("batchStop", batchStop);
        } else {
            batchStart = batchStop + 1;
            batchStop = (Integer) batchStart + items.size() - 1;
            stepExecution.getExecutionContext().put("batchStart", batchStart);
            stepExecution.getExecutionContext().put("batchStop", batchStop);
        }

        RawItem firstItem = items.get(0);
        Resource firstItemResource = firstItem.getResource();

        if (currentResource == null || !(this.currentResource.equals(firstItemResource))) {
            this.orderedHeaders = writeHeader(firstItem);
            this.currentResource = firstItemResource;
        }

        StringBuilder insertSql = new StringBuilder();
        insertSql.append("INSERT INTO stg_" + tableName[0] + " (batch_id,");
        StringBuilder valuesSqlBuilder = new StringBuilder();
        valuesSqlBuilder.append(" VALUES (?,");
        for (String header : this.orderedHeaders) {
            insertSql.append(header).append(",");
            valuesSqlBuilder.append("?").append(",");
        }
        insertSql.setLength(insertSql.length() - 1); // trim comma
        valuesSqlBuilder.setLength(valuesSqlBuilder.length() - 1); // trim comma
        insertSql.append(")");
        valuesSqlBuilder.append(");");
        insertSql.append(valuesSqlBuilder);

        final AtomicInteger batchStartRef = new AtomicInteger(batchStart);
        final String sql = insertSql.toString();
        jdbcTemplate.getJdbcOperations().execute(sql, new PreparedStatementCallback() {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                for (RawItem item : items) {
                    final List<Object> paramsForLog = new ArrayList(orderedHeaders.length);
                    int counter = 1;
                    paramsForLog.add(batchStartRef.get());
                    StatementCreatorUtils.setParameterValue(ps, counter, SqlTypeValue.TYPE_UNKNOWN, batchStartRef.getAndIncrement());
                    counter++;
                    for ( String header : orderedHeaders ) {
                        final Map<String, String> record = item.getRecord();
                        String value = record.get(header);
                        final Integer sqlType = metadataRepository
                            .getRepository()
                            .getColumnMetadataRepository()
                            .getColumnMetadata(
                                    new ColumnReference(tableName[0], header))
                            .getJavaSqlType();
                        paramsForLog.add(value);
                        StatementCreatorUtils.setParameterValue(ps, counter, sqlType, value);
                        counter++;
                    }
                    sayQuery(sql, paramsForLog);
                    ps.addBatch();
                }
                return ps.executeBatch();
            }
        });
        batchStart = batchStartRef.get();

        say("******CHUNK POSTGRES******");
    }

    
    @OnSkipInWrite
    private void saveCurrentResource(RawItem item, Throwable t)
    {
        String fileName = currentResource.getFilename();
        String[] tableName = fileName.split("\\.");
        stepExecution.getExecutionContext().put("currentEntity", tableName[0]);
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

    private void sayQuery(String sql, List<Object> bindParams) {
        queryLogger.info("Query: [{}] Bind Params: [{}]", sql, bindParams);
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

    @Override
    public void beforeStep(StepExecution arg0) {
        this.stepExecution = arg0;
    }

    @Override
    public ExitStatus afterStep(StepExecution arg0) {
        return ExitStatus.COMPLETED;
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}

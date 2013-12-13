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

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jarbframework.utils.orm.ColumnReference;
import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.domain.RawItem;
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

public class SqlServerStagingTableWriter implements ItemWriter<RawItem>,
        StepExecutionListener {

    private Resource currentResource;
    private String[] orderedHeaders = null;
    private MetadataConfigurations metadataRepository;
    private StepExecution stepExecution;

    private static final Logger logger = LoggerFactory.getLogger(SqlServerStagingTableWriter.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void write(List<? extends RawItem> items) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<String> batchedStatements = new ArrayList<String>();
        String fileName = items.get(0).getResource().getFilename();
        String[] fileNameSplit = fileName.split("\\.");

        Integer batchStart = (Integer) (stepExecution.getExecutionContext()
                .get("batchStart") == null ? null : stepExecution
                .getExecutionContext().get("batchStart"));
        Integer batchStop = (Integer) (stepExecution.getExecutionContext().get(
                "batchStop") == null ? null : stepExecution
                .getExecutionContext().get("batchStop"));
        Object currentEntity = stepExecution.getExecutionContext().get(
                "currentEntity");

        String tableName = fileNameSplit[0];
        if (currentEntity == null || !currentEntity.equals(tableName)) {
            batchStart = 0;
            batchStop = items.size() - 1;
            currentEntity = tableName;
            stepExecution.getExecutionContext().put("currentEntity", tableName);
            stepExecution.getExecutionContext().put("batchStart", batchStart);
            stepExecution.getExecutionContext().put("batchStop", batchStop);
        } else {
            batchStart = batchStop + 1;
            batchStop = (Integer) batchStart + items.size() - 1;
            stepExecution.getExecutionContext().put("batchStart", batchStart);
            stepExecution.getExecutionContext().put("batchStop", batchStop);
        }

        if (currentResource == null) {
            this.orderedHeaders = writeHeader(items.get(0));
            this.currentResource = items.get(0).getResource();
        }

        for (RawItem item : items) {
            Resource itemResource = item.getResource();
            if (!(this.currentResource.equals(itemResource))) {
                say();
                this.orderedHeaders = writeHeader(item);
                this.currentResource = itemResource;
            }
            StringBuilder insertSql = new StringBuilder();
            insertSql.append("INSERT INTO stg_" + tableName + " (batch_id,");

            StringBuilder valuesSqlBuilder = new StringBuilder();
            valuesSqlBuilder.append(" VALUES ( " + batchStart + ",");
            final Map<String, String> record = item.getRecord();
            for (String header : this.orderedHeaders) {
                String value;
                Integer sqlType = metadataRepository
                        .getRepository()
                        .getColumnMetadataRepository()
                        .getColumnMetadata(
                                new ColumnReference(tableName, header))
                        .getJavaSqlType();
                if (isQuotedType(sqlType) && record.get(header) != null) {
                    value = "'" + record.get(header) + "'";
                } else {
                    value = record.get(header);
                }
                insertSql.append(header).append(",");
                valuesSqlBuilder.append(value).append(",");
            }
            insertSql.setLength(insertSql.length() - 1); // trim comma
            valuesSqlBuilder.setLength(valuesSqlBuilder.length() - 1); // trim
                                                                       // comma
            insertSql.append(")");
            valuesSqlBuilder.append(");");
            insertSql.append(valuesSqlBuilder);
            batchedStatements.add(insertSql.toString());
            batchStart++;
            say(insertSql);
        }
        jdbcTemplate.batchUpdate(batchedStatements.toArray(new String[]{}));
        say("******CHUNK POSTGRES******");
    }

    private boolean isQuotedType(Integer sqlType) {
        return Types.CHAR == sqlType || Types.DATE == sqlType
                || Types.LONGNVARCHAR == sqlType
                || Types.LONGVARCHAR == sqlType || Types.NCHAR == sqlType
                || Types.NVARCHAR == sqlType || Types.TIME == sqlType
                || Types.TIMESTAMP == sqlType || Types.VARCHAR == sqlType;
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

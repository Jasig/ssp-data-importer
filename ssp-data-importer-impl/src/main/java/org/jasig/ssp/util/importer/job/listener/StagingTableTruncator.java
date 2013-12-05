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
package org.jasig.ssp.util.importer.job.listener;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.staging.PostgresExternalTableUpsertWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;

public class StagingTableTruncator implements StepExecutionListener {

    private DataSource dataSource;

    private MetadataConfigurations metadataRepository;
    
    private List<String> stagingTables = new ArrayList<String>();
    
    private String truncateExclusions;
    
    private static final Logger logger = LoggerFactory.getLogger(StagingTableTruncator.class);
    

    @Override
    public ExitStatus afterStep(StepExecution arg0) {
        return ExitStatus.COMPLETED;
    }

    private boolean isNotExcluded(String[] exclusions, String table) {
        for (String exclusion : exclusions) {
            if(exclusion.equalsIgnoreCase(table))
                return false;
        }
        return true;
    }

    @Override
    public void beforeStep(StepExecution arg0) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            dataSource.getConnection().setAutoCommit(true);
            ResultSet tables = dataSource.getConnection().getMetaData().getTables(null, null, "stg_%", new String[]{"TABLE"});
            String[] exclusions = truncateExclusions == null ? new String[]{} : truncateExclusions.split(",");
            while(tables.next())
            {
                stagingTables.add(tables.getString("table_name"));
            }
            for (String table : stagingTables) {
                if(isNotExcluded(exclusions,table))
                {
                    String sql = "truncate table "+table+";";
                    jdbcTemplate.execute(sql);
                    logger.info(sql);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        logger.info("DONE TRUNCATE");

    }

    public MetadataConfigurations getMetadataRepository() {
        return metadataRepository;
    }

    public void setMetadataRepository(MetadataConfigurations metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getTruncateExclusions() {
        return truncateExclusions;
    }

    public void setTruncateExclusions(String truncateExclusions) {
        this.truncateExclusions = truncateExclusions;
    }





}

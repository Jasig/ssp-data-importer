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

import java.util.ArrayList;
import java.util.List;

import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.report.ErrorEntry;
import org.jasig.ssp.util.importer.job.report.StepType;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;


public class StagingAndUpsertSkipListener implements SkipListener<RawItem, RawItem> {

    private StepExecution stepExecution;
    
    private MetadataConfigurations metadataRepository;

    
    private static final Logger logger = LoggerFactory.getLogger(StagingAndUpsertSkipListener.class);
    @Override
    public void onSkipInRead(Throwable t) {
        logger.error("ERROR on Upsert Read", t);
    }

    @Override
    public void onSkipInWrite(RawItem item, Throwable t) {
        reportOnError(item, t);
    }

    @Override
    public void onSkipInProcess(RawItem item, Throwable t) {
        logger.error("ERROR on Stage/Upsert Process", t);
        reportOnError(item, t);
    }

    
    @SuppressWarnings("unchecked")
    private void reportOnError(RawItem item, Throwable t) {
        logger.error("ERROR on Stage/Upsert Write", t);
        StepContext stepContext = StepSynchronizationManager.getContext();
        Integer readCount = stepContext.getStepExecution().getCommitCount();
        Integer lineNumberError = stepContext.getStepExecution().getWriteSkipCount();
        lineNumberError = readCount + lineNumberError;
        
        String fileName = item.getResource().getFilename();
        String[] tableName = fileName.split("\\.");
        stepExecution.getExecutionContext().put("currentEntity",
                tableName[0]);  
        List<String> tableKeys = metadataRepository.getRepository()
                .getColumnMetadataRepository()
                .getTableMetadata(new TableReference( tableName[0])).getTableKeys();

        // There are a few external tables that don't (yet) have natural keys,
        // in these cases we've enforced the key on the staging table
        // so in cases where the external table does not have any keys, we look
        // towards the corresponding staging table for them
        if (tableKeys.isEmpty()) {
            tableKeys = metadataRepository.getRepository().getColumnMetadataRepository()
                    .getTableMetadata(new TableReference("stg_" +  tableName[0]))
                    .getTableKeys();
        }
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append("Entity Keys: {");
        for (String key : tableKeys) {
            keyBuilder.append(key).append(" : ").append(item.getRecord().get(key)).append(", ");
        }
        keyBuilder.append("}");
        ErrorEntry error = new ErrorEntry(tableName[0],keyBuilder.toString(),t.getCause().toString(),StepType.STAGEUPSERT);
        error.setLineNumber(readCount.toString());
        List<ErrorEntry> errors =(List<ErrorEntry>) stepExecution.getJobExecution().getExecutionContext().get("errors");
        if(errors == null)
        {
            errors = new ArrayList<ErrorEntry>();
        }
        errors.add(error);
        stepExecution.getJobExecution().getExecutionContext().put("errors", errors);
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

    public MetadataConfigurations getMetadataRepository() {
        return metadataRepository;
    }

    public void setMetadataRepository(MetadataConfigurations metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

}

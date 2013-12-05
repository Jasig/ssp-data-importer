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

import java.util.HashMap;
import java.util.Map;

import org.jasig.ssp.util.importer.job.report.ReportEntry;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;


public class StagingAndUpsertListener implements StepExecutionListener {

    private StepExecution stepExecution;
    
  
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
    public void beforeStep(StepExecution stepExecution) {
        
    }

    @SuppressWarnings("unchecked")
    @Override
    @AfterStep
    public ExitStatus afterStep(StepExecution arg0) {
        Integer numInsertedUpdated = (Integer) stepExecution.getExecutionContext()
                .get("numInsertedUpdated");
        String currentEntity = (String) stepExecution.getExecutionContext().get(
                "currentEntity");
      
        if(currentEntity != null)
        {
            Map<String, ReportEntry> report =  (Map<String, ReportEntry>) stepExecution.getJobExecution()
                    .getExecutionContext().get("report");
            if(report == null)
            {
                report = new HashMap<String,ReportEntry>(); 
            }
            
            ReportEntry currentEntry = new ReportEntry(currentEntity,numInsertedUpdated == null ? 0 : numInsertedUpdated);
            report.put(currentEntity, currentEntry);
            stepExecution.getJobExecution().getExecutionContext().put("report", report);            
        }

        return ExitStatus.COMPLETED;
    }

}

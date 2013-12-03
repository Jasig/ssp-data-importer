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

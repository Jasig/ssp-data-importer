package org.jasig.ssp.util.importer.job.listener;

import java.util.ArrayList;
import java.util.List;

import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.report.ErrorEntry;
import org.jasig.ssp.util.importer.job.report.StepType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;


public class StagingAndUpsertSkipListener implements SkipListener<RawItem, RawItem> {

    private StepExecution stepExecution;
    
    Logger logger = LoggerFactory.getLogger(StagingAndUpsertSkipListener.class);
    @Override
    public void onSkipInRead(Throwable t) {
        logger.error("ERROR on Upsert Read", t);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSkipInWrite(RawItem item, Throwable t) {
        logger.error("ERROR on Upsert Write", t);
        
        String fileName = item.getResource().getFilename();
        String[] tableName = fileName.split("\\.");
        ErrorEntry error = new ErrorEntry(tableName[0],item.getRecord().toString(),t.getMessage(),StepType.STAGE);
        List<ErrorEntry> errors =(List<ErrorEntry>) stepExecution.getJobExecution().getExecutionContext().get("errors");
        if(errors == null)
        {
            errors = new ArrayList<ErrorEntry>();
        }
        errors.add(error);
        stepExecution.getJobExecution().getExecutionContext().put("errors", errors);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSkipInProcess(RawItem item, Throwable t) {
        logger.error("ERROR on Upsert Process", t);
        
        String fileName = item.getResource().getFilename();
        String[] tableName = fileName.split("\\.");
        ErrorEntry error = new ErrorEntry(tableName[0],item.getRecord().toString(),t.getMessage(),StepType.STAGE);
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

}

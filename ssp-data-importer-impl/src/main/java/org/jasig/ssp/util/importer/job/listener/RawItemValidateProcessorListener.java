package org.jasig.ssp.util.importer.job.listener;

import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.TableViolationException;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;

public class RawItemValidateProcessorListener implements ItemProcessListener<RawItem, RawItem> {

    Boolean hasTableViolation = false;
    @Override
    public void beforeProcess(RawItem item) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterProcess(RawItem item, RawItem result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProcessError(RawItem item, Exception e) {
        if(!e.getClass().equals(TableViolationException.class) || hasTableViolation == false){
            StepContext stepContext = StepSynchronizationManager.getContext();
            Integer readCount = stepContext.getStepExecution().getReadCount();
            Integer skipedCount = stepContext.getStepExecution().getSkipCount();
            skipedCount = readCount + skipedCount;
            System.out.println("line:" + skipedCount.toString() + " " + e.getMessage());
        }
        if(e.getClass().equals(TableViolationException.class) && hasTableViolation == false){
            hasTableViolation = true;
        }
    }

}

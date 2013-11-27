package org.jasig.ssp.util.importer.job.listener;

import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.tasklet.BatchFinalizer;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.TableViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;

public class RawItemValidateProcessorListener implements ItemProcessListener<RawItem, RawItem> {

    Boolean hasTableViolation = false;
    Logger logger = LoggerFactory.getLogger(BatchFinalizer.class);
    @Override
    public void beforeProcess(RawItem item) {
        StepContext stepContext = StepSynchronizationManager.getContext();

    }

    @Override
    public void afterProcess(RawItem item, RawItem result) {
        StepContext stepContext = StepSynchronizationManager.getContext();
    }

    @Override
    public void onProcessError(RawItem item, Exception e) {
        if(!e.getClass().equals(TableViolationException.class) || hasTableViolation == false){
            StepContext stepContext = StepSynchronizationManager.getContext();
            Integer readCount = stepContext.getStepExecution().getReadCount();
            Integer skipedCount = stepContext.getStepExecution().getSkipCount();
            skipedCount = readCount + skipedCount;
            logger.error("line:" + skipedCount.toString() + " " + e.getMessage());
        }
        if(e.getClass().equals(TableViolationException.class) && hasTableViolation == false){
            hasTableViolation = true;
        }
    }

}

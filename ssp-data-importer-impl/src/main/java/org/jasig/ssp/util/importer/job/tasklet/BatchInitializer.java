package org.jasig.ssp.util.importer.job.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class BatchInitializer implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // TODO port logic from Groovy impl to copy files from the monitored directory to the scratch/work directory,
        // cleaning out the latter first
        return RepeatStatus.FINISHED;
    }
}

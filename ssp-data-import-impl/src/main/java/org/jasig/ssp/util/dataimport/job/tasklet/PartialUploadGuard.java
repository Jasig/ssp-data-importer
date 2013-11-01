package org.jasig.ssp.util.dataimport.job.tasklet;


import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class PartialUploadGuard implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // TODO port logic from Groovy impl to abandon the job if there are partial uploads in the monitored directory
        return RepeatStatus.FINISHED;
    }
}

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

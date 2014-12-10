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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.report.ErrorEntry;
import org.jasig.ssp.util.importer.job.report.StepType;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.ViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;

/**
 * WriteListener is being leveraged to process write errors as they happen instead of
 * waiting for a skip listener to process them.  The skip listener waits until right before
 * a commit to happen to process(which will not happen if a rollback is called).
 **/

public class WriteListener implements ItemWriteListener<RawItem> {

    private static final  Logger logger = LoggerFactory.getLogger(WriteListener.class);
    private ExecutionContext executionContext;

    @Override
    public void beforeWrite(List<? extends RawItem> items) {
    }

    @Override
    public void afterWrite(List<? extends RawItem> items) {
    }

    @Override
    public void onWriteError(Exception e, List<? extends RawItem> items) {
        if (e.getClass().equals(ViolationException.class)){
            ViolationException violation = (ViolationException) e;
            String EOL = System.getProperty("line.separator");
            logger.error("ERROR on Write, line:" + violation.getLineNumber() + EOL + e.getMessage());
        } else {
            logger.error("ERROR on Write" + e.getMessage());
        }
        putErrorInJobContext(e, items);        
    }

    @SuppressWarnings("unchecked")
    public void putErrorInJobContext(Throwable t, List<? extends RawItem> items) {
        String lineNumber = null;
        if(t instanceof ViolationException) {
            lineNumber = ((ViolationException) t).getLineNumber() != null ? ((ViolationException) t).getLineNumber().toString() : null;
        }
        RawItem lastItem = null;
        String fileName = null;
        String tableName = null;
        String record = null;
        if (items != null && !items.isEmpty()) {
            lastItem = items.get(items.size() - 1);
            fileName = lastItem.getResource().getFilename();
            tableName = fileName.split("\\.")[0];
            record = lastItem.getRecord().toString();
        }

        ErrorEntry error = new ErrorEntry(tableName, record, t.getMessage(), StepType.WRITE);
        error.setLineNumber(lineNumber);
        List<ErrorEntry> errors =(List<ErrorEntry>) executionContext.get("errors");
        if(errors == null) {
            errors = new ArrayList<ErrorEntry>();
        }
        errors.add(error);
        executionContext.put("errors", errors);
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.executionContext = stepExecution.getJobExecution().getExecutionContext();
    }

}

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

import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.report.ErrorEntry;
import org.jasig.ssp.util.importer.job.report.StepType;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.TableViolationException;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.ViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;

/**
 * ProcessListener is being leveraged to process process errors as they happen instead of
 * waiting for a skip listener to process them.  The skip listener waits until right before
 * a commit to happen to process(which will not happen if a rollback is called).
 **/

public class RawItemValidateProcessorListener implements ItemProcessListener<RawItem, RawItem> {

    private static final  Logger logger = LoggerFactory.getLogger(RawItemValidateProcessorListener.class);
    Boolean hasTableViolation = false;
    private ExecutionContext executionContext;
    private final static String EOL = System.getProperty("line.separator");

    @Override
    public void beforeProcess(RawItem item) {
    }

    @Override
    public void afterProcess(RawItem item, RawItem result) {
    }

    @Override
    public void onProcessError(RawItem item, Exception e) {
        if (!e.getClass().equals(TableViolationException.class) && !e.getClass().equals(ViolationException.class) || hasTableViolation == false) {
            logger.error("ERROR on Upsert Process: " + EOL + e.getMessage());
        } else if (e.getClass().equals(TableViolationException.class)){
            hasTableViolation = true;
            ViolationException violation = (TableViolationException) e;
            logger.error("ERROR on Upsert Process, line:" + violation.getLineNumber() + EOL + e.getMessage());
        }
        putErrorInJobContext(item, e);
    }

    @SuppressWarnings("unchecked")    public void putErrorInJobContext(RawItem item, Throwable t) {
        String lineNumber = null;
        if(t instanceof ViolationException) {
            lineNumber = ((ViolationException) t).getLineNumber() != null ? ((ViolationException) t).getLineNumber().toString() : null;
        }
        
        String fileName = item.getResource().getFilename();
        String[] tableName = fileName.split("\\.");
        ErrorEntry error = new ErrorEntry(tableName[0], item.getRecord().toString(), t.getMessage(), StepType.VALIDATE);
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

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
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.ViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;

/**
 * ReadListener is being leveraged to process read errors as they happen instead of
 * waiting for a skip listener to process them.  The skip listener waits until right before
 * a commit to happen to process(which will not happen if a rollback is called).
 **/

public class ReadListener implements ItemReadListener<RawItem> {

    private static final  Logger logger = LoggerFactory.getLogger(ReadListener.class);
    private ExecutionContext executionContext;
    
    @Override
    public void beforeRead() {
    }

    @Override
    public void afterRead(RawItem item) {
    }

    @Override
    public void onReadError(Exception e) {
        if (e.getClass().equals(ViolationException.class)) {
            ViolationException violation = (ViolationException) e;
            String EOL = System.getProperty("line.separator");
            logger.error("ERROR on Read, line:" + violation.getLineNumber() + EOL + e.getMessage());
        } else {
            logger.error("ERROR on Read" + e.getMessage());
        }
        putErrorInJobContext(e);        
    }
    
    @SuppressWarnings("unchecked")
    public void putErrorInJobContext(Throwable t) {
        String lineNumber = null;
        if(t instanceof ViolationException)
        {
            lineNumber = ((ViolationException) t).getLineNumber() != null ? ((ViolationException) t).getLineNumber().toString() : null;
        }
        ErrorEntry error = new ErrorEntry(null, null, t.getMessage(), StepType.READ);
        error.setLineNumber(lineNumber);
        List<ErrorEntry> errors = (List<ErrorEntry>) executionContext.get("errors");
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

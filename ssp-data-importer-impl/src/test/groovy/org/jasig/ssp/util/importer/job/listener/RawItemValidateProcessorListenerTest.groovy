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
package org.jasig.ssp.util.importer.job.listener

import spock.lang.Specification
import org.springframework.batch.core.StepExecution
import org.jasig.ssp.util.importer.job.domain.RawItem
import org.springframework.batch.core.JobExecution
import org.springframework.batch.item.ExecutionContext
import org.springframework.core.io.Resource
import org.jasig.ssp.util.importer.job.report.StepType
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.ViolationException

class RawItemValidateProcessorListenerTest extends Specification {
   def rawItemValidateProcessorListener
   def executionContext
   def stepExecution
   def rawItem
   def exception
   def violationException
   def jobExecution
   def resource
   def record
   def errors
   def violationMessage = "Test Violation Exception"
   
   def setup() {
       // Class being tested
       rawItemValidateProcessorListener = new RawItemValidateProcessorListener()
       
       // Unmocked Map and List
       record = [:]
       errors = []
       
       // Mocks
       stepExecution = Mock(StepExecution)
       rawItem = Mock(RawItem)
       exception = Mock(Exception)
       violationException = Mock(ViolationException)
       jobExecution = Mock(JobExecution)
       executionContext = Mock(ExecutionContext)
       resource = Mock(Resource)
       
       // Supply Mock Responses
       stepExecution.getJobExecution() >> jobExecution
       jobExecution.getExecutionContext() >> executionContext
       executionContext.get("errors") >> errors
       rawItem.getResource() >> resource
       rawItem.getRecord() >> record
       resource.getFilename() >> "test_file.csv"
       violationException.getLineNumber() >> 5
       violationException.getMessage() >> violationMessage
   }

   def "test execution context is updated with error when onProcessError is called"() {
       given: "Set up step execution context before error is thrown"
       rawItemValidateProcessorListener.saveStepExecution(stepExecution);
       
       when: "Step throws an Exception and calls onProcessError"
       rawItemValidateProcessorListener.onProcessError(rawItem, exception)
       
       then: "Exception is added to error stack"
       1 * executionContext.put("errors", errors)
       assert errors.size() == 1
       def error = errors.get(0)
       assert error.getLineNumber() == null
       assert error.getTableName() == "test_file"
       assert error.getMessage() == null
       assert error.getStepType() == StepType.VALIDATE
   }
   
   def "test the error pushed into the execution context has the correct details when onProcessError is called"() {
       given: "Set up step execution context before error is thrown"
       rawItemValidateProcessorListener.saveStepExecution(stepExecution);
       
       when: "Step throws an Exception and calls onProcessError"
       rawItemValidateProcessorListener.onProcessError(rawItem, violationException)
       
       then: "Exception is added to error stack"
       1 * executionContext.put("errors", errors)
       assert errors.size() == 1
       def error = errors.get(0)
       assert error.getLineNumber() == "5"
       assert error.getTableName() == "test_file"
       assert error.getMessage() == violationMessage
       assert error.getStepType() == StepType.VALIDATE
   }
}
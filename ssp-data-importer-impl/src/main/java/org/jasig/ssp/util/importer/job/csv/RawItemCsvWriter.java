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
package org.jasig.ssp.util.importer.job.csv;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

public class RawItemCsvWriter<RawItem> extends FlatFileItemWriter<RawItem> implements StepExecutionListener{


    private Resource writeDirectory;
    private static final Logger logger = LoggerFactory.getLogger(RawItemCsvWriter.class);
    private String delimiter = DelimitedLineTokenizer.DELIMITER_COMMA;

    public RawItemCsvWriter(Resource writeDirectory) throws Exception{
         this.setExecutionContextName(ClassUtils.getShortName(RawItemCsvWriter.class));
        this.writeDirectory = writeDirectory;
        createDirectory();
    }

    @Override
    public void open(ExecutionContext executionContext){
        RawItemFlatFileHeaderCallback headerCallBack = new RawItemFlatFileHeaderCallback();
        String[] columnNames = (String[])executionContext.get("COLUMNS_NAMES_KEY");
        headerCallBack.setColumnNames(columnNames);
        headerCallBack.setDelimiter(delimiter);
        super.setHeaderCallback(headerCallBack);

        RawItemLineAggregator aggregator = new RawItemLineAggregator();
        aggregator.setColumnNames(columnNames);
        aggregator.setDelimiter(delimiter);
        super.setLineAggregator((LineAggregator<RawItem>)aggregator);
        super.open(executionContext);
    }

    @Override
    public void setResource(Resource resource){
        String fileName = resource.getFilename();
        try {
           File file = new File(writeDirectory.getFile(), fileName);
           file.createNewFile();
           if(file.exists())
               logger.info("File created for writing file name: " + file);
           else
               logger.error("File not created for writing file name: " + file);
           super.setResource(new FileSystemResource(file));
        } catch (IOException e) {
            logger.error("Error attempting to create file " + fileName + "for writing raw output.", e);
            e.printStackTrace();
        }

    }

    private void createDirectory() throws Exception{
        File dir = writeDirectory.getFile();
        if(!dir.exists())
            if(!dir.mkdirs())
                throw new Exception("process directory not created");
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("Start Raw Write Step for file " + stepExecution.getExecutionContext().getString("fileName") );
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("End Raw Write Step for " + stepExecution.getExecutionContext().getString("fileName") +
                " lines read: " +
                stepExecution.getReadCount() +
                " lines skipped: " + stepExecution.getReadSkipCount());
        logger.info(stepExecution.getSummary());
        return ExitStatus.COMPLETED;
    }

    public void setDelimiter(String delimiter){
        this.delimiter = delimiter;
    }


}


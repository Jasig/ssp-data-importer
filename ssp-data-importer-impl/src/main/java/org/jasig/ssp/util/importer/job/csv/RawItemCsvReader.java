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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;

public class RawItemCsvReader extends FlatFileItemReader<RawItem> implements StepExecutionListener, LineCallbackHandler, FieldSetMapper<RawItem> {

    private static final Logger logger = LoggerFactory.getLogger(RawItemCsvReader.class);
    final private String COLUMN_NAMES_KEY = "COLUMNS_NAMES_KEY";
    private StepExecution stepExecution;
    private DefaultLineMapper<RawItem> lineMapper;
    private String[] columnNames;
    private Resource itemResource;
    private String delimiter = DelimitedLineTokenizer.DELIMITER_COMMA;
    private char quoteCharacter = DelimitedLineTokenizer.DEFAULT_QUOTE_CHARACTER;


    public RawItemCsvReader() {
        setLinesToSkip(1);
        setSkippedLinesCallback(this);
    }

    @Override
    public void afterPropertiesSet() {
        // not in constructor to ensure we invoke the override
        final DefaultLineMapper<RawItem> lineMapper = new DefaultLineMapper<RawItem>();
        setLineMapper(lineMapper);
    }

    /**
     * Satisfies {@link LineCallbackHandler} contract and and Acts as the {@code skippedLinesCallback}.
     *
     * @param line
     */
    @Override
    public void handleLine(String line) {
        getLineMapper().setLineTokenizer(getTokenizer(line));
        getLineMapper().setFieldSetMapper(this);
    }

    private LineTokenizer getTokenizer(String line){
        this.columnNames = line.split(delimiter);
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setQuoteCharacter(quoteCharacter);
        lineTokenizer.setDelimiter(delimiter);
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(columnNames);
        addColumnNames();
        return lineTokenizer;
    }

    private void addColumnNames(){
        stepExecution.getExecutionContext().put(COLUMN_NAMES_KEY, columnNames);
    }

    @Override
    public void setResource(Resource resource){
        //No longer using MultiResource Reader
        this.itemResource = resource;
        super.setResource(resource);
    }

    /**
     * Provides acces to an otherwise hidden field in parent class. We need this because we have to reconfigure
     * the {@link LineMapper} based on file contents.
     * @param lineMapper
     */
    @Override
    public void setLineMapper(LineMapper<RawItem> lineMapper) {
        if ( !(lineMapper instanceof DefaultLineMapper) ) {
            throw new IllegalArgumentException("Must specify a DefaultLineMapper");
        }
        this.lineMapper = (DefaultLineMapper)lineMapper;

        super.setLineMapper(lineMapper);
    }

    private DefaultLineMapper getLineMapper() {
        return this.lineMapper;
    }

    /**
     * Satisfies {@link FieldSetMapper} contract.
     * @param fs
     * @return
     * @throws BindException
     */
    @Override
    public RawItem mapFieldSet(FieldSet fs) throws BindException {
        if ( fs == null ) {
            return null;
        }
        Map<String,String> record = new LinkedHashMap<String, String>();
        for (String columnName : this.columnNames) {
            record.put(columnName, StringUtils.trimToNull(fs.readString(columnName)));
        }
        RawItem item = new RawItem();
        item.setResource(itemResource);
        item.setRecord(record);
        // TODO for now we're not worrying about setting the Resource b/c we happen to know the wrapping
        // MultiResourceItemReader will do it for us and there's no accessible getter on our super class. But
        // would be better to do it here.
        return item;
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("Start Raw Read Step for " + itemResource.getFilename());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("End Raw Read Step for " + itemResource.getFilename() +
                "lines read: " +
                stepExecution.getReadCount() +
                "lines skipped: " + stepExecution.getReadSkipCount());
        return ExitStatus.COMPLETED;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }


    public void setQuoteCharacter(char quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }
}

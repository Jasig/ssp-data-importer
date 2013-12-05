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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineAggregator;


public class RawItemLineAggregator implements LineAggregator<RawItem> {

    private String delimiter = DelimitedLineTokenizer.DELIMITER_COMMA;
    private char quoteCharacter = DelimitedLineTokenizer.DEFAULT_QUOTE_CHARACTER;
    private String[] columnNames;
    private int INITIAL_STRING_SIZE = 512;
    /** The quote constant to use when you wish to suppress all quoting and escaping, they must be the same for support csv standard. */
    private static final char NO_QUOTE_CHARACTER = '\u0000';

    private Logger logger = LoggerFactory.getLogger(RawItemLineAggregator.class);

    public RawItemLineAggregator() {
    }

    @Override
    public String aggregate(RawItem item) {

        Map<String,String> itemMap = item.getRecord();

        if(columnNames == null){
            if(item.getResource() != null)
                logger.error("Column Names Not Found. Unable to process table: " + item.getResource().getFilename());
            else
                logger.error("Column Names Not Found. Unable to process table, no resource found." + item.getResource().getFilename());
            return "No Columns";
        }

        String[] values = new String[columnNames.length];
        int i = 0;
        for(String key: columnNames){
            String value = itemMap.get(key);
            if(!StringUtils.isBlank(value)){
                values[i++] = value;
            }else{
                values[i++] = null;
            }
        }
        return writeNext(values);
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }
    /**
     * Public setter for the delimiter.
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setQuoteCharacter(char quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

 public String writeNext(String[] nextLine) {

        if (nextLine == null)
            return "";

        StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
        for (int i = 0; i < nextLine.length; i++) {

            if (i != 0) {
                sb.append(delimiter);
            }

            String nextElement = nextLine[i];
            if (StringUtils.isBlank(nextElement))
                continue;
            if (quoteCharacter !=  NO_QUOTE_CHARACTER)
                sb.append(quoteCharacter);

            sb.append(stringContainsSpecialCharacters(nextElement) ? processLine(nextElement) : nextElement);

            if (quoteCharacter != NO_QUOTE_CHARACTER)
                sb.append(quoteCharacter);
        }

        return sb.toString();
    }

    private boolean stringContainsSpecialCharacters(String line) {
        return line.indexOf(quoteCharacter) != -1;
    }

    protected StringBuilder processLine(String nextElement)
    {
        StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
        for (int j = 0; j < nextElement.length(); j++) {
            char nextChar = nextElement.charAt(j);
            if (quoteCharacter != NO_QUOTE_CHARACTER && nextChar == quoteCharacter) {
                sb.append(quoteCharacter).append(nextChar);
            } else {
                sb.append(nextChar);
            }
        }

        return sb;
    }
}

package org.jasig.ssp.util.importer.job.csv;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class RawItemFlatFileHeaderCallback implements FlatFileHeaderCallback {

    String[] columnNames;
    private String delimiter = DelimitedLineTokenizer.DELIMITER_COMMA;
    private Logger logger = LoggerFactory.getLogger(RawItemFlatFileHeaderCallback.class);

    public RawItemFlatFileHeaderCallback() {
       super();
    }

    @Override
    public void writeHeader(Writer writer) throws IOException {
        StringBuffer header = new StringBuffer();

        if(columnNames == null){
            logger.error("Column names not found");
            throw new IOException("Unable to write table, column names not found");
        }

        for(String columnName:columnNames){
            header.append(columnName).append(delimiter);
        }

        writer.write(StringUtils.chop(header.toString()));
    }


    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public void setDelimiter(String delimiter){
        this.delimiter = delimiter;
    }
}

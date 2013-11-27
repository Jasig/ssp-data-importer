package org.jasig.ssp.util.importer.job.csv;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.springframework.batch.item.file.transform.LineAggregator;


public class RawItemLineAggregator implements LineAggregator<RawItem> {

    private String delimiter = ",";
    private String[]columnNames;

    public RawItemLineAggregator() {
    }

    @Override
    public String aggregate(RawItem item) {

        StringBuffer line = new StringBuffer();
        Map<String,String> itemMap = item.getRecord();
        if(columnNames == null){
            //TODO ADD logging and exceptions
            return "No Columns";
        }
        for(String key: columnNames){
            if(!StringUtils.isBlank(itemMap.get(key)))
                line.append(itemMap.get(key));
            line.append(delimiter);
        }
        return StringUtils.chop(line.toString());
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

}

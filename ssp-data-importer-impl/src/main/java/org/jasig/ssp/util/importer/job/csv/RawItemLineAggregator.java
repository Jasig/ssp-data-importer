package org.jasig.ssp.util.importer.job.csv;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jasig.ssp.util.importer.job.domain.RawItem;

public class RawItemLineAggregator implements ItemLineAggregator<RawItem> {

    String delimiter = ",";
    List<String> columns;

    public RawItemLineAggregator() {
    }

    @Override
    public String aggregate(RawItem item) {

        StringBuffer line = new StringBuffer();
        Map<String,String> itemMap = item.getRecord();

        if(columns == null){
            //TODO ADD logging and exceptions
            return "No Columns";
        }
        for(String key: columns){
            if(!StringUtils.isBlank(itemMap.get(key)))
                line.append(itemMap.get(key));
            line.append(delimiter);
        }
        return StringUtils.chop(line.toString());
    }

    /**
     * Public setter for the delimiter.
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public void setOrderedColumns(List<String> columns) {

        this.columns = columns;
    }

}

package org.jasig.ssp.util.importer.job.csv;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jasig.ssp.util.importer.job.domain.RawItem;

public class RawItemFlatFileHeaderCallback implements ItemFlatFileHeaderCallBack<RawItem> {

    List<String> columnNames;
    String  delimiter;

    public RawItemFlatFileHeaderCallback() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void writeHeader(Writer writer) throws IOException {
        StringBuffer header = new StringBuffer();

        for(String columnName:columnNames){
            header.append(columnName).append(delimiter);
        }

        writer.write(StringUtils.chop(header.toString()));
    }


    @Override
    public void setColumnDescriptorObject(RawItem item) {
        columnNames = new ArrayList<String>();
        if(item == null){
            //TODO add log and exception
            return;
        }
        for(String key:item.getRecord().keySet()){
            columnNames.add(key);
        }
    }

    @Override
    public List<String> getOrderedColumnList() {
        return columnNames;
    }

    public void setDelimiter(String delimiter){
        this.delimiter = delimiter;
    }


}

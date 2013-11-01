package org.jasig.ssp.util.dataimport.job.csv;

import org.jasig.ssp.util.dataimport.job.domain.RawItemAndBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RawItemAndBeanCsvWriter implements ItemWriter<RawItemAndBean> {

    private Resource currentResource;
    private String[] orderedHeaders = null;

    @Override
    public void write(List<? extends RawItemAndBean> items) throws Exception {
        // TODO Extend FlatFileItemWriter instead. For now we're just writing to std out for prototyping.
        // As written, this only works if the bean is step-scoped. Might be a bit challenging even in a
        // FlatFileItemWriter subclass to write an item-based header row.
        if ( items == null ) {
            currentResource = null;
            orderedHeaders = null;
            return;
        }
        if ( currentResource == null || !(currentResource.equals(items.get(0).getRawItem().getResource())) ) {
            if ( currentResource != null ) {
                say("");
            }
            orderedHeaders = writeHeader(items);
        }
        for ( RawItemAndBean item : items ) {
            StringBuilder sb = new StringBuilder();
            final Map<String,String> record = item.getRawItem().getRecord();
            for ( String header : orderedHeaders ) {
                sb.append(record.get(header)).append(",");
            }
            sb.setLength(sb.length() - 1); // trim comma
            say(sb);
        }
    }

    private String[] writeHeader(List<? extends RawItemAndBean> items) {
        Map<String,String> firstRecord = items.get(0).getRawItem().getRecord();
        StringBuilder sb = new StringBuilder();
        List<String> headerColumns = new ArrayList<String>();
        for ( String key : firstRecord.keySet() ) {
            sb.append(key).append(",");
            headerColumns.add(key);
        }
        sb.setLength(sb.length() - 1); // trim comma
        say(sb);
        return headerColumns.toArray(new String[headerColumns.size()]);
    }

    private void say(Object message) {
        System.out.println(message);
    }
}

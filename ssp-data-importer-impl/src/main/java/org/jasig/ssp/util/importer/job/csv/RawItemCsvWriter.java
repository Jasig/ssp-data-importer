package org.jasig.ssp.util.importer.job.csv;

import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.springframework.batch.item.ItemWriter;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RawItemCsvWriter implements ItemWriter<RawItem> {

    private Resource currentResource;
    private String[] orderedHeaders = null;

    @Override
    public void write(List<? extends RawItem> items) throws Exception {
        // TODO Extend FlatFileItemWriter instead. For now we're just writing to std out for prototyping.
        // As written, this only works if the bean is step-scoped. Might be a bit challenging even in a
        // FlatFileItemWriter subclass to write an item-based header row.
        if ( items == null ) {
            this.currentResource = null;
            this.orderedHeaders = null;
            return;
        }
        if ( currentResource == null ) {
            this.orderedHeaders = writeHeader(items.get(0));
            this.currentResource = items.get(0).getResource();
        }
        for ( RawItem item : items ) {
            Resource itemResource = item.getResource();
            if ( !(this.currentResource.equals(itemResource)) ) {
                say();
                this.orderedHeaders = writeHeader(item);
                this.currentResource = itemResource;
            }
            StringBuilder sb = new StringBuilder();
            final Map<String,String> record = item.getRecord();
            for ( String header : this.orderedHeaders ) {
                sb.append(record.get(header)).append(",");
            }
            sb.setLength(sb.length() - 1); // trim comma
            say(sb);
        }
    }

    private String[] writeHeader(RawItem item) {
        Map<String,String> firstRecord = item.getRecord();
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

    private void say() {
        say("");
    }
}

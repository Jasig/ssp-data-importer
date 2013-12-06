package org.jasig.ssp.util.importer.job.csv;

import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;

public class RawItemLineMapper extends DefaultLineMapper<RawItem> implements LineMapper<RawItem> {

    public RawItemLineMapper() {
        super();
    }

    @Override
    public RawItem mapLine(String line, int lineNumber) throws Exception {
        RawItem item = super.mapLine(line, lineNumber);
        item.setLineNumber(lineNumber);
        return item;
    }

}

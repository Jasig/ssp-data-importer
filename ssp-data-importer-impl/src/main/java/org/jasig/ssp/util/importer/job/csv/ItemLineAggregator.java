package org.jasig.ssp.util.importer.job.csv;

import java.util.List;

import org.springframework.batch.item.file.transform.LineAggregator;

public interface ItemLineAggregator<T> extends LineAggregator<T> {

    public void setOrderedColumns(List<String> columns);
}

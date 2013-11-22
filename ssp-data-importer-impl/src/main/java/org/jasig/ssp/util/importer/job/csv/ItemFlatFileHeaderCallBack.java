package org.jasig.ssp.util.importer.job.csv;

import java.util.List;

import org.springframework.batch.item.file.FlatFileHeaderCallback;

public interface ItemFlatFileHeaderCallBack<T> extends FlatFileHeaderCallback {

    public void setColumnDescriptorObject(T item);

    public List<String> getOrderedColumnList();

}

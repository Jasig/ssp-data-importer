package org.jasig.ssp.util.dataimport.job.domain;

import org.springframework.core.io.Resource;

import java.util.Map;

/**
 * Holds a {@link RawItem} and an arbitrary object associated with it, typically a SSP entity bean onto which it was
 * mapped.
 */
public class RawItemAndBean extends RawItem {

    private RawItem rawItem;
    private Object bean;

    public RawItem getRawItem() {
        return rawItem;
    }

    public void setRawItem(RawItem rawItem) {
        this.rawItem = rawItem;
    }

    @Override
    public void setResource(Resource resource) {
        if ( rawItem == null ) {
            rawItem = new RawItem();
        }
        rawItem.setResource(resource);
    }

    @Override
    public Resource getResource() {
        return rawItem == null ? null : rawItem.getResource();
    }

    @Override
    public void setRecord(Map<String,String> record) {
        if ( rawItem == null ) {
            rawItem = new RawItem();
        }
        rawItem.setRecord(record);
    }

    public Map<String,String> getRecord() {
        return rawItem == null ? null : rawItem.getRecord();
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

}

package org.jasig.ssp.util.dataimport.job.domain;

/**
 * Holds a {@link RawItem} and an arbitrary object associated with it, typically a SSP entity bean onto which it was
 * mapped.
 */
public class RawItemAndBean {

    private RawItem rawItem;
    private Object bean;

    public RawItem getRawItem() {
        return rawItem;
    }

    public void setRawItem(RawItem rawItem) {
        this.rawItem = rawItem;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

}

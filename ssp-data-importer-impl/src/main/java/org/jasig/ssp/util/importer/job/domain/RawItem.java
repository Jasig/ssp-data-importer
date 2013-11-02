package org.jasig.ssp.util.importer.job.job.domain;

import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

import java.util.Map;

public class RawItem implements ResourceAware {

    private Resource resource;
    private Map<String,String> record;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Map<String, String> getRecord() {
        return record;
    }

    public void setRecord(Map<String, String> record) {
        this.record = record;
    }

}

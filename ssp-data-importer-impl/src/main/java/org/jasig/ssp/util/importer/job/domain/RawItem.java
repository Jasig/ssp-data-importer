package org.jasig.ssp.util.importer.job.domain;

import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.util.HashMap;
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

    public Map<String,String> getRecordAsPropertyMap(){
        Map<String,String> recordAsProperty = new HashMap<String,String>();

        for(String columnName:record.keySet()){
            String[] particles =  columnName.split("_");
            String propertyName = particles[0].toLowerCase();
            for(Integer i = 1; i < particles.length; i++) {
                propertyName +=  StringUtils.capitalize(particles[i]);
            }
            recordAsProperty.put(propertyName, record.get(columnName));
        }
        return recordAsProperty;
    }

}

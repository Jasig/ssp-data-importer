/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

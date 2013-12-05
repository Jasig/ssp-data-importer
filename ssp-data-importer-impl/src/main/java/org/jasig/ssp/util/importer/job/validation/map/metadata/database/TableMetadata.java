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
package org.jasig.ssp.util.importer.job.validation.map.metadata.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;

public class TableMetadata {
    private List<String> tableKeys = new ArrayList<String>();

    private final TableReference tableReference;


    public TableMetadata(org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference tableReference) {
        super();
        this.tableReference = tableReference;
    }

    public List<String> getTableKeys() {
        return tableKeys;
    }

    public void addKey(String tableKey) {
        this.tableKeys.add(tableKey);
    }

    public Boolean hasKeys(Map<String,String> tableMap){
        for(String tableKey: tableKeys){
            if(!tableMap.containsKey(tableKey))
                return false;
        }
        return true;
    }

    public List<String> missingKeys(Map<String,String> tableMap){
        List<String> missingKeys = new ArrayList<String>();
        for(String tableKey: tableKeys){
            if(!tableMap.containsKey(tableKey))
                missingKeys.add(tableKey);
        }
        return missingKeys;
    }

    public TableReference getTableReference(){
        return tableReference;
    }
}

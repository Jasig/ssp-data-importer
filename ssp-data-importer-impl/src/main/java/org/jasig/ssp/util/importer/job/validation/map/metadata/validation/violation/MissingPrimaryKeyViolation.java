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
package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation;

import java.util.List;

import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapViolation;

public class MissingPrimaryKeyViolation implements MapViolation {

    String violation;

    MapReference mapReference;

    List<String> missingKeys;
    public MissingPrimaryKeyViolation(MapReference mapReference, List<String> missingKeys){
        this.mapReference = mapReference;
        this.missingKeys = missingKeys;

        //super(mapReference, stringBuilder.toString(), "Header missing key column, unable to process");
    }

    @Override
    public String getViolation() {
        // TODO Auto-generated method stub
        return violation;
    }

    @Override
    public void setViolation(String violation) {
        this.violation = violation;
    }

    public String getTableName(){
        return mapReference.getTableName();
    }

    @Override
    public String buildMessage() {
        return "Header missing key column, unable to process  for table"
                + mapReference.getTableName() + " " + buildColumnList();
    }

    private String buildColumnList(){
        StringBuilder stringBuilder = new StringBuilder("missing columns: ");
        for(String missingKey:missingKeys ){
            stringBuilder.append(missingKey + ":");
        }
        return stringBuilder.toString();
    }

    @Override
    public String buildShortMessage() {
        return buildMessage();
    }

    @Override
    public Boolean isTableViolation() {
        // TODO Auto-generated method stub
        return true;
    }

}

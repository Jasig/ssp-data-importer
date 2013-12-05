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

import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapViolation;

public class GenericMapViolation implements MapViolation {

    private String violation;

    private Object columnValue;

    private MapReference mapReference;

    public GenericMapViolation(MapReference mapReference, Object columnValue, String violation) {
        super();
        this.violation = violation;
        this.mapReference = mapReference;
        this.columnValue = columnValue;
    }

    public String getViolation() {
        return violation;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }

    public String buildMessage(){
        return mapReference.toString() + " "  + this.violation + " value:" + columnValue;
    }

    public String buildShortMessage(){
        return mapReference.getName() + " " + this.violation + " value:" + columnValue;
    }

    public String getTableName(){
        return mapReference.getTableName();
    }

    @Override
    public Boolean isTableViolation() {
        return false;
    }
}

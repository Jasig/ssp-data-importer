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
package org.jasig.ssp.util.importer.job.report;

import java.io.Serializable;

public class ErrorEntry implements Serializable {

    public ErrorEntry(String tableName, String entityString, String message,StepType stepType) {
        super();
        this.tableName = tableName;
        this.entityString = entityString;
        this.message = message;
        this.stepType = stepType;
    }

    private static final long serialVersionUID = -1804360014858522956L;

    private String tableName;

    private String entityString;

    private String message;
    
    private StepType stepType;
    
    private String lineNumber;
    
    @Override
    public String toString() {
        String EOL = System.getProperty("line.separator");
        if(stepType.equals(StepType.STAGEUPSERT))
        {
            return "Table Name: "+tableName+EOL+"Line Number: "+lineNumber+EOL+" Entity: "+entityString+EOL+" Step: "+stepType.name()+EOL+" Message: "+message;
        }
        else
        {
            return "Table Name: "+tableName+EOL+"Line Number: "+lineNumber+EOL+" Step: "+stepType.name()+EOL+" Message: "+message;

        }
    };

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getEntityString() {
        return entityString;
    }

    public void setEntityString(String entityString) {
        this.entityString = entityString;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StepType getStepType() {
        return stepType;
    }

    public void setStepType(StepType stepType) {
        this.stepType = stepType;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

}

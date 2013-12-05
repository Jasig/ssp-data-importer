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

public class ReportEntry implements Serializable {
    
    private static final long serialVersionUID = -913112234221221239L;

    public ReportEntry(String tableName, Integer numberInserted) {
        super();
        this.tableName = tableName;
        this.numberInsertedUpdated = numberInserted;
    }

    private String tableName;
    
    private Integer numberInsertedUpdated;
    
    @Override
    public String toString() {
        return "Table Name: "+tableName+" Number Inserted/Updated: "+numberInsertedUpdated; 
    };

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getNumberInsertedUpdated() {
        return numberInsertedUpdated;
    }

    public void setNumberInsertedUpdated(Integer numberInsertedUpdated) {
        this.numberInsertedUpdated = numberInsertedUpdated;
    }


}

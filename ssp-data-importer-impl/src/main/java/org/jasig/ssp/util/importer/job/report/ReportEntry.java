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

    private static String FORMAT = "%1$-50s %2$-20s %3$-30s %4$-20s %5$-20s";

    public ReportEntry(String tableName, Integer numberInserted) {
        super();
        this.tableName = tableName;
        this.numberInsertedUpdated = numberInserted;
    }

    public ReportEntry() {
    }

    private String tableName;

    private Integer numberInsertedUpdated;

    private Integer numberParsed;

    private Integer numberSkippedOnDatabaseWrite;

    private Integer numberSkippedOnParse;

    @Override
    public String toString() {

        return String.format(FORMAT, "Table Name: "+tableName,"Parsed: "+getNumberParsed(),"Skipped on Parse: "+getNumberSkippedOnParse()
                ,"Upserted: "+getNumberInsertedUpdated(),"Skipped on Write: "+getNumberSkippedOnDatabaseWrite());
    };

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getNumberInsertedUpdated() {
        return numberInsertedUpdated == null ? 0:numberInsertedUpdated;
    }

    public void setNumberInsertedUpdated(Integer numberInsertedUpdated) {
        this.numberInsertedUpdated = numberInsertedUpdated;
    }

    public Integer getNumberParsed() {
        return numberParsed == null ? 0:numberParsed;
    }

    public void setNumberParsed(Integer numberParsed) {
        this.numberParsed = numberParsed;
    }

    public Integer getNumberSkippedOnDatabaseWrite() {
        return numberSkippedOnDatabaseWrite == null ? 0:numberSkippedOnDatabaseWrite;
    }

    public void setNumberSkippedOnDatabaseWrite(
            Integer numberSkippedOnDatabaseWrite) {
        this.numberSkippedOnDatabaseWrite = numberSkippedOnDatabaseWrite;
    }

    public Integer getNumberSkippedOnParse() {
        return numberSkippedOnParse == null ? 0:numberSkippedOnParse;
    }

    public void setNumberSkippedOnParse(Integer numberSkippedOnParse) {
        this.numberSkippedOnParse = numberSkippedOnParse;
    }


}

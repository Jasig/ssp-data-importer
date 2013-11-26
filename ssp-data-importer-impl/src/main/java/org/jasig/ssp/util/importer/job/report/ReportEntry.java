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

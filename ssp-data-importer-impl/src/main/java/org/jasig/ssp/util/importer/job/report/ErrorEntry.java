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
    
    @Override
    public String toString() {
        return "Table Name: "+tableName+" Entity: "+entityString+" Step: "+stepType.name()+" Message: "+message;
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

}

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

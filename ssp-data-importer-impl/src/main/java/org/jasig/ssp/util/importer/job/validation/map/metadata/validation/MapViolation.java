package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;

public class MapViolation {

    private String violation;

    private MapReference mapReference;



    public MapViolation(MapReference mapReference, String violation) {
        super();
        this.violation = violation;
        this.mapReference = mapReference;
    }

    public String getViolation() {
        return violation;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }

    public String buildMessage(){
        return mapReference.toString() + this.violation;
    }

}

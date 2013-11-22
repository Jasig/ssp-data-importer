package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation;

import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;

public class LengthConstraintMapViolation extends GenericMapViolation {

    public LengthConstraintMapViolation(MapReference mapReference, Object columnValue){
        super(mapReference, columnValue, "length constraint violated.");
    }
}

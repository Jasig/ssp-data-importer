package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation;

import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;

public class FractionLengthConstraintMapViolation extends GenericMapViolation {

    public FractionLengthConstraintMapViolation(MapReference mapReference, Object columnValue) {
        super(mapReference, columnValue, "Fraction Length Constraint Violate:");
    }

}

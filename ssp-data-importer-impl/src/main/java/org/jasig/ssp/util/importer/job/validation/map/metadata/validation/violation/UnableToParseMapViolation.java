package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation;

import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;

public class UnableToParseMapViolation extends GenericMapViolation {

    public UnableToParseMapViolation(MapReference mapReference, Object columnValue, String javaSqlType) {
        super(mapReference, columnValue, "Unable to parse based on type ("+ javaSqlType + ")");
    }
}

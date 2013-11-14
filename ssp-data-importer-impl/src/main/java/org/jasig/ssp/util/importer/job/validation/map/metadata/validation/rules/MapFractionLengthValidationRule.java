package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.rules;

import java.math.BigDecimal;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.constraint.validation.DatabaseConstraintValidationContext;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.DatabaseConstraintMapValidationContext;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapViolation;

class MapFractionLengthValidationRule implements MapValueValidationRule {

    private static final String FRACTION_LENGTH_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.FractionLength.message}";

    @Override
    public void validate(String columnValue, MapReference MapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (fractionLengthExceeded(columnValue, columnMetadata)) {
            context.addViolation(new MapViolation(MapReference, FRACTION_LENGTH_TEMPLATE));
        }
    }

    private boolean fractionLengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
        boolean lengthExceeded = false;
        if ((columnMetadata.hasFractionLength()) && (propertyValue instanceof Number)) {
            lengthExceeded = lengthOfFraction((Number) propertyValue) > columnMetadata.getFractionLength();
        }
        return lengthExceeded;
    }

    private int lengthOfFraction(Number number) {
        BigDecimal numberAsBigDecimal = new BigDecimal(number.toString());
        return numberAsBigDecimal.scale() < 0 ? 0 : numberAsBigDecimal.scale();
    }

}

package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import java.math.BigDecimal;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.FractionLengthConstraintMapViolation;

public class FractionLengthConstraintMapValidationStep implements DatabaseConstraintMapValidationStep {

    @Override
    public void validate(Object columnValue, MapReference mapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (isFractionLengthExceeded(columnValue, columnMetadata)) {
            context.addViolation(new FractionLengthConstraintMapViolation(mapReference, columnValue));
        }
    }

    private boolean isFractionLengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
        boolean lengthExceeded = false;
        if (columnMetadata.hasFractionLength() && propertyValue instanceof Number) {
            int fractionLength = getFractionLength((Number) propertyValue);
            lengthExceeded = fractionLength > columnMetadata.getFractionLength();
        }
        return lengthExceeded;
    }

    private int getFractionLength(Number number) {
        BigDecimal numberAsBigDecimal = new BigDecimal(number.toString());
        return numberAsBigDecimal.scale() < 0 ? 0 : numberAsBigDecimal.scale();
    }

}

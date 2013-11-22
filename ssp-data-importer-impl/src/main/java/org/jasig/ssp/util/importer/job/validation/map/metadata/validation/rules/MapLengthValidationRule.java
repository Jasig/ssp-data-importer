package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.rules;

import java.math.BigDecimal;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.DatabaseConstraintMapValidationContext;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapViolation;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.LengthConstraintMapViolation;

class MapLengthValidationRule implements MapValueValidationRule {

    @Override
    public void validate(Object propertyValue, MapReference MapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (lengthExceeded(propertyValue, columnMetadata)) {
            context.addViolation(new LengthConstraintMapViolation(MapReference, propertyValue));
        }
    }

    private boolean lengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
        boolean lengthExceeded = false;
        if (columnMetadata.hasMaximumLength()) {
            if (propertyValue instanceof String) {
                lengthExceeded = ((String) propertyValue).length() > columnMetadata.getMaximumLength();
            } else if (propertyValue instanceof Number) {
                lengthExceeded = numberOfDigits((Number) propertyValue) > columnMetadata.getMaximumLength();
            }
        }
        return lengthExceeded;
    }

    private int numberOfDigits(Number number) {
        return new BigDecimal(number.toString()).precision();
    }

}

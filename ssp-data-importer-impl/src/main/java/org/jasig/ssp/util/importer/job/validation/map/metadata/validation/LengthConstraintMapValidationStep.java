package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import java.math.BigDecimal;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.LengthConstraintMapViolation;

public class LengthConstraintMapValidationStep implements DatabaseConstraintMapValidationStep {

    @Override
    public void validate(Object columnValue, MapReference mapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (isLengthExceeded(columnValue, columnMetadata)) {
            context.addViolation(new LengthConstraintMapViolation(mapReference, columnValue));
        }
    }

    private boolean isLengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
        boolean lengthExceeded = false;
        if (columnMetadata.hasMaximumLength()) {
            int length = getLength(propertyValue);
            lengthExceeded = length > columnMetadata.getMaximumLength();
        }
        return lengthExceeded;
    }

    private int getLength(Object value) {
        int length = -1;
        if (value instanceof String) {
            length = ((String) value).length();
        } else if (value instanceof Number) {
            length = new BigDecimal(value.toString()).precision();
        }
        return length;
    }

}

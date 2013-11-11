package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.rules;

import java.math.BigDecimal;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.DatabaseConstraintMapValidationContext;

class MapLengthValidationRule implements MapValueValidationRule {

    private static final String LENGTH_VIOLATION_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.Length.message}";

    @Override
    public void validate(String propertyValue, MapReference MapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (lengthExceeded(propertyValue, columnMetadata)) {
            context.buildViolationWithTemplate(MapReference, LENGTH_VIOLATION_TEMPLATE)
                        .attribute("max", columnMetadata.getMaximumLength())
                        .value(propertyValue)
                            .addToContext();
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

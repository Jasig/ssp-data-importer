package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import java.math.BigDecimal;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;

public class FractionLengthConstraintMapValidationStep implements DatabaseConstraintMapValidationStep {

    private static final String FRACTION_LENGTH_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.FractionLength.message}";

    @Override
    public void validate(String propertyValue, MapReference propertyRef, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (isFractionLengthExceeded(propertyValue, columnMetadata)) {
            context.buildViolationWithTemplate(propertyRef, FRACTION_LENGTH_TEMPLATE)
                    .attribute("max", columnMetadata.getFractionLength())
                    .value(propertyValue)
                        .addToContext();
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

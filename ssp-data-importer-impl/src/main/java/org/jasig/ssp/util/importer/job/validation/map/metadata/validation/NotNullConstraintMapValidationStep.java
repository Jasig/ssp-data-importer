package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;

public class NotNullConstraintMapValidationStep implements DatabaseConstraintMapValidationStep {

    private static final String NOT_NULL_VIOLATION_TEMPLATE = "{javax.validation.constraints.NotNull.message}";

    @Override
    public void validate(Object propertyValue, MapReference mapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (propertyValue == null && isValueExpected(mapReference, columnMetadata)) {
            context.addViolation(new MapViolation(mapReference, NOT_NULL_VIOLATION_TEMPLATE));
        }
    }

    private boolean isValueExpected(MapReference propertyRef, ColumnMetadata columnMetadata) {
        return columnMetadata.isRequired() && ! isGeneratable(propertyRef, columnMetadata);
    }

    private boolean isGeneratable(MapReference propertyRef, ColumnMetadata columnMetadata) {
        return columnMetadata.isGeneratable();
    }

}

package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;

public class NotNullConstraintMapValidationStep implements DatabaseConstraintMapValidationStep {

    private static final String NOT_NULL_VIOLATION_TEMPLATE = "{javax.validation.constraints.NotNull.message}";

    @Override
    public void validate(String propertyValue, MapReference propertyRef, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (propertyValue == null && isValueExpected(propertyRef, columnMetadata)) {
            context.buildViolationWithTemplate(propertyRef, NOT_NULL_VIOLATION_TEMPLATE).addToContext();
        }
    }

    private boolean isValueExpected(MapReference propertyRef, ColumnMetadata columnMetadata) {
        return columnMetadata.isRequired() && ! isGeneratable(propertyRef, columnMetadata);
    }

    private boolean isGeneratable(MapReference propertyRef, ColumnMetadata columnMetadata) {
        return columnMetadata.isGeneratable();
    }

}

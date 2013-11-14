package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.rules;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.DatabaseConstraintMapValidationContext;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapViolation;

class MapNotNullValidationRule implements MapValueValidationRule {

    private static final String NOT_NULL_VIOLATION_TEMPLATE = "{javax.validation.constraints.NotNull.message}";

    @Override
    public void validate(String propertyValue, MapReference MapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (propertyValue == null && valueIsExpected(MapReference, columnMetadata)) {
            context.addViolation(new MapViolation(MapReference, NOT_NULL_VIOLATION_TEMPLATE));
        }
    }

    private boolean valueIsExpected(MapReference MapReference, ColumnMetadata columnMetadata) {
        return columnMetadata.isRequired() && !isGeneratable(MapReference, columnMetadata);
    }

    private boolean isGeneratable(MapReference MapReference, ColumnMetadata columnMetadata) {
        return columnMetadata.isGeneratable();
    }

}

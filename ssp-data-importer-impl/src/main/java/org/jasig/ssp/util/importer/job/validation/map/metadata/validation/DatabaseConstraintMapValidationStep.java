package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;

/**
 * Database constraint validation step that can be performed during a validate.
 * @author Jeroen van Schagen
 * @since 19-10-2011
 */
public interface DatabaseConstraintMapValidationStep {

    /**
     * Validates a property value on database column constraints.
     * @param propertyValue the property value to validate
     * @param propertyRef reference to the property
     * @param columnMetadata metadata of the column referenced by our property
     * @param validation the validation result in which violations are stored
     */
    void validate(String propertyValue, MapReference propertyRef, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context);

}

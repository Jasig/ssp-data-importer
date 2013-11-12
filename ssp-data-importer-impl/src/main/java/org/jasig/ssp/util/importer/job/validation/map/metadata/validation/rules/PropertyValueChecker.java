package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.rules;

import java.util.ArrayList;
import java.util.List;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.constraint.validation.DatabaseConstraintValidationContext;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.DatabaseConstraintMapValidationContext;

/**
 * Validates a property value on the column metadata.
 * @author Jeroen van Schagen
 * @since 20-10-2012
 */
public class PropertyValueChecker {

    /** Concrete validation logic that should be performed **/
    private final List<MapValueValidationRule> validationRules;

    public PropertyValueChecker() {
        validationRules = new ArrayList<MapValueValidationRule>();
        validationRules.add(new MapNotNullValidationRule());
        validationRules.add(new MapLengthValidationRule());
        validationRules.add(new MapFractionLengthValidationRule());
    }

    public void validate(String propertyValue, MapReference MapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext validation) {
        for(MapValueValidationRule validationRule : validationRules) {
            validationRule.validate(propertyValue, MapReference, columnMetadata, validation);
        }
    }

}

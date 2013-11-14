package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext;

import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapViolationMessageBuilder.MapViolationMessageTemplate;

/**
 * Context of a specific bean validation. Bean violations can be retrieved and included to this context.
 *
 * @author Jeroen van Schagen
 * @since 20-10-2011
 */
public final class DatabaseConstraintMapValidationContext {



    /** Describes if this context is still valid, meaning it has no violations. **/
    private boolean valid = true;
    private List<MapViolation> violations = new ArrayList<MapViolation>();

    DatabaseConstraintMapValidationContext() {

    }

    /**
     * Determine if no violations were detected in this validation context.
     * @return {@code true} if no violations were detected, else {@code false}
     */
    public boolean isValid() {
        return valid;
    }

    public void addViolation(MapViolation violation){
        violations.add(violation);
        valid = false;
    }

    public List<MapViolation> getViolations(){
        return violations;
    }
}

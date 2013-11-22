package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation;

import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapConstraintValidatorContext;

public class ViolationException extends Exception {

    private MapConstraintValidatorContext violation;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ViolationException() {
        // TODO Auto-generated constructor stub
    }

    public ViolationException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public ViolationException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public ViolationException(MapConstraintValidatorContext violation) {
        super();
        this.violation = violation;
    }

    public ViolationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    public MapConstraintValidatorContext getConstraintValidationContext() {
        return violation;
    }

    public void setValidationConstraintContext(MapConstraintValidatorContext violation) {
        this.violation = violation;
    }

    @Override
    public String getMessage(){
        return violation.buildShortViolationMessage();
    }

}

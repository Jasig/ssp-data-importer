package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation;

import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapConstraintValidatorContext;

public class TableViolationException extends ViolationException {

    /**
     *
     */
    private static final long serialVersionUID = 3741256992914469281L;

    public TableViolationException() {
        // TODO Auto-generated constructor stub
    }

    public TableViolationException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public TableViolationException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public TableViolationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    public TableViolationException(MapConstraintValidatorContext violation) {
        super(violation);
    }

}

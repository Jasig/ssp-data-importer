package org.jasig.ssp.util.importer.job.staging;
/**
 * Because we execute the upsert as part of the 'staging' step and want an error on upsert to 
 * be fatal.  We create a special exception that will be thrown on upsert what is configured in 
 * the step as excluded.  This will allow skip tolerance on staging where we expect some errors
 * but not want to fail fatally while still failing fast on upsert.
 */
public class NotSkippableException extends RuntimeException{

    public NotSkippableException() {
        super();
    }

    public NotSkippableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSkippableException(String message) {
        super(message);
    }

    public NotSkippableException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = -7146535941635015199L;

}

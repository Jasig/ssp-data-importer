package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;


public interface MapViolation {


    public String getViolation();

    public void setViolation(String violation);

    public String buildMessage();

    /* can be anything but for writing out message without table name*/
    public String buildShortMessage();

    public String getTableName();

    public Boolean isTableViolation();

}

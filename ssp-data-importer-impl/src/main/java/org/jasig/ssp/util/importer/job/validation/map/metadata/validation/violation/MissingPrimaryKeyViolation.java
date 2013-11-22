package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation;

import java.util.List;

import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapViolation;

public class MissingPrimaryKeyViolation implements MapViolation {

    String violation;

    MapReference mapReference;

    List<String> missingKeys;
    public MissingPrimaryKeyViolation(MapReference mapReference, List<String> missingKeys){
        this.mapReference = mapReference;
        this.missingKeys = missingKeys;

        //super(mapReference, stringBuilder.toString(), "Header missing key column, unable to process");
    }

    @Override
    public String getViolation() {
        // TODO Auto-generated method stub
        return violation;
    }

    @Override
    public void setViolation(String violation) {
        this.violation = violation;
    }

    public String getTableName(){
        return mapReference.getTableName();
    }

    @Override
    public String buildMessage() {
        return "Header missing key column, unable to process  for table"
                + mapReference.getTableName() + " " + buildColumnList();
    }

    private String buildColumnList(){
        StringBuilder stringBuilder = new StringBuilder("missing columns: ");
        for(String missingKey:missingKeys ){
            stringBuilder.append(missingKey + ":");
        }
        return stringBuilder.toString();
    }

    @Override
    public String buildShortMessage() {
        return buildMessage();
    }

    @Override
    public Boolean isTableViolation() {
        // TODO Auto-generated method stub
        return true;
    }

}

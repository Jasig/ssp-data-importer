package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintValidatorContext;
import javax.validation.metadata.ConstraintDescriptor;

import org.apache.commons.logging.Log;
import org.hibernate.validator.engine.MessageAndPath;
import org.hibernate.validator.engine.PathImpl;
import org.hibernate.validator.util.LoggerFactory;
import org.jasig.ssp.util.importer.job.validation.map.metadata.MapConstraintDescriptor;



public class MapConstraintValidatorContext implements
        ConstraintValidatorContext {

    //private static final Log log = LoggerFactory.make();

    private boolean defaultDisabled = false;
    private DatabaseConstraintMapValidationContext context;


    public MapConstraintValidatorContext() {
    }


    @Override
    public void disableDefaultConstraintViolation() {
        defaultDisabled = true;

    }


    @Override
    public String getDefaultConstraintMessageTemplate() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public ConstraintViolationBuilder buildConstraintViolationWithTemplate(
            String messageTemplate) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setContext(DatabaseConstraintMapValidationContext context){
        this.context = context;
    }

    public List<MapViolation> getViolations(){
        return context.getViolations();
    }

    public String buildViolationMessage(){
        StringBuilder violationMessage = new StringBuilder();
        for(MapViolation violation:context.getViolations()){
            violationMessage.append(violation.buildMessage() + " ");
        }
        return violationMessage.toString();
    }

    public String buildShortViolationMessage(){
        StringBuilder violationMessage = new StringBuilder();
        if(context.getViolations().size() > 0){
            violationMessage.append("from table: " +
                    context.getViolations().get(0).getTableName() + " ");
        }
        for(MapViolation violation:context.getViolations()){
            violationMessage.append(violation.buildShortMessage() + " ");
        }
        return violationMessage.toString();
    }

    public Boolean hasTableViolation(){
        for(MapViolation violation:context.getViolations()){
            if(violation.isTableViolation())
                return true;
        }
        return false;
    }

}

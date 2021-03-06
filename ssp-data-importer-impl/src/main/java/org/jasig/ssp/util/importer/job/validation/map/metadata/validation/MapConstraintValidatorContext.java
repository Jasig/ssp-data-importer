/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import java.util.List;

import javax.validation.ConstraintValidatorContext;



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

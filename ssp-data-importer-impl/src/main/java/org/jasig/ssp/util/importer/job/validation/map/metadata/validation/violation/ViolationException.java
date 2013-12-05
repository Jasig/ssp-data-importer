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

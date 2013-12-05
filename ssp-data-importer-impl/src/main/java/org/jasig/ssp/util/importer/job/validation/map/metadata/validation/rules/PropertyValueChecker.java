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

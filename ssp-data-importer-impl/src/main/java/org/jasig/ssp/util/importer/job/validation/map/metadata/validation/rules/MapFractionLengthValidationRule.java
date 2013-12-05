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

import java.math.BigDecimal;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.constraint.validation.DatabaseConstraintValidationContext;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.DatabaseConstraintMapValidationContext;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapViolation;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.FractionLengthConstraintMapViolation;

class MapFractionLengthValidationRule implements MapValueValidationRule {

    @Override
    public void validate(Object columnValue, MapReference MapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (fractionLengthExceeded(columnValue, columnMetadata)) {
            context.addViolation(new FractionLengthConstraintMapViolation(MapReference, columnValue));
        }
    }

    private boolean fractionLengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
        boolean lengthExceeded = false;
        if ((columnMetadata.hasFractionLength()) && (propertyValue instanceof Number)) {
            lengthExceeded = lengthOfFraction((Number) propertyValue) > columnMetadata.getFractionLength();
        }
        return lengthExceeded;
    }

    private int lengthOfFraction(Number number) {
        BigDecimal numberAsBigDecimal = new BigDecimal(number.toString());
        return numberAsBigDecimal.scale() < 0 ? 0 : numberAsBigDecimal.scale();
    }

}

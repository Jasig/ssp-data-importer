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

import java.math.BigDecimal;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.LengthConstraintMapViolation;

public class LengthConstraintMapValidationStep implements DatabaseConstraintMapValidationStep {

    @Override
    public void validate(Object columnValue, MapReference mapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (isLengthExceeded(columnValue, columnMetadata)) {
            context.addViolation(new LengthConstraintMapViolation(mapReference, columnValue));
        }
    }

    private boolean isLengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
        boolean lengthExceeded = false;
        if (columnMetadata.hasMaximumLength()) {
            int length = getLength(propertyValue);
            lengthExceeded = length > columnMetadata.getMaximumLength();
        }
        return lengthExceeded;
    }

    private int getLength(Object value) {
        int length = -1;
        if (value instanceof String) {
            length = ((String) value).length();
        } else if (value instanceof Number) {
            length = new BigDecimal(value.toString()).precision();
        }
        return length;
    }

}

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

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.NotNullViolation;

public class NotNullConstraintMapValidationStep implements DatabaseConstraintMapValidationStep {

    @Override
    public void validate(Object propertyValue, MapReference mapReference, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context) {
        if (propertyValue == null && isValueExpected(mapReference, columnMetadata)) {
            context.addViolation(new NotNullViolation(mapReference, propertyValue));
        }
    }

    private boolean isValueExpected(MapReference propertyRef, ColumnMetadata columnMetadata) {
        return columnMetadata.isRequired() && ! isGeneratable(propertyRef, columnMetadata);
    }

    private boolean isGeneratable(MapReference propertyRef, ColumnMetadata columnMetadata) {
        return columnMetadata.isGeneratable();
    }

}

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

/**
 * Database constraint validation step that can be performed during a validate.
 * @author Jeroen van Schagen
 * @since 19-10-2011
 */
public interface DatabaseConstraintMapValidationStep {

    /**
     * Validates a property value on database column constraints.
     * @param propertyValue the property value to validate
     * @param propertyRef reference to the property
     * @param columnMetadata metadata of the column referenced by our property
     * @param validation the validation result in which violations are stored
     */
    void validate(Object propertyValue, MapReference propertyRef, ColumnMetadata columnMetadata, DatabaseConstraintMapValidationContext context);

}

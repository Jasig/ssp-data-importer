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

import org.jarbframework.utils.spring.SpringBeanFinder;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.TableColumnMetaDataRepository;

/**
 * Constructs {@link DatabaseConstraintValidator} beans.
 *
 * @author Jeroen van Schagen
 * @since 20-10-2011
 */
public class DatabaseConstraintMapValidatorFactory {

    public static final String DEFAULT_SCHEMA_MAPPER_ID = "schemaMapper";
    public static final String DEFAULT_COLUMN_METADATA_REPOSITORY_ID = "databaseConstraintRepository";
    public static final String DEFAULT_VALIDATOR_FACTORY_ID = "validator";

    private final SpringBeanFinder beanFinder;

    public DatabaseConstraintMapValidatorFactory(SpringBeanFinder beanFinder) {
        this.beanFinder = beanFinder;
    }

    /**
     * Build a new {@link DatabaseConstraintValidator}.
     * @return the database constraint validation bean
     */
    public DatabaseConstraintMapValidator build() {
        DatabaseConstraintMapValidator validator = new DatabaseConstraintMapValidator();
        validator.setColumnMetadataRepository(beanFinder.findBean(TableColumnMetaDataRepository.class, null, DEFAULT_COLUMN_METADATA_REPOSITORY_ID));
        return validator;
    }

}

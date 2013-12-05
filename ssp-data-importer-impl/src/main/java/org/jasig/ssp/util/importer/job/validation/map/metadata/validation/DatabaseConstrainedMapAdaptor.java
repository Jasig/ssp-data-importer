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

import javax.validation.ConstraintValidatorContext;

import org.jarbframework.constraint.validation.DatabaseConstrained;
import org.jarbframework.utils.spring.SpringBeanFinder;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DatabaseConstrainedMapAdaptor implements  ApplicationContextAware{

        /** Delegate component that performs the actual constraint checking **/
        private DatabaseConstraintMapValidator validator;

        /** Used to lookup beans in our application context **/
        private SpringBeanFinder beanFinder;


        public boolean isValid(MapReference mapReference, ConstraintValidatorContext validatorContext) {
            return validator.isValid(mapReference, validatorContext);
        }

        public void initialize(DatabaseConstrained annotation) {
            validator = buildValidator(annotation);
        }

        private DatabaseConstraintMapValidator buildValidator(DatabaseConstrained annotation) {
            try {
                return beanFinder.findBean(DatabaseConstraintMapValidator.class, annotation.id());
            } catch (NoSuchBeanDefinitionException nsbde) {
                // Create a new validation bean when none are registered
                return new DatabaseConstraintMapValidatorFactory(beanFinder).build();
            }
        }

        public void setApplicationContext(ApplicationContext applicationContext) {
            this.beanFinder = new SpringBeanFinder(applicationContext);
        }

}

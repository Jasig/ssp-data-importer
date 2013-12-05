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
package org.jasig.ssp.util.importer.job.config;

import org.jarbframework.constraint.metadata.database.JdbcColumnMetadataRepository;
import org.jarbframework.utils.spring.SpringBeanFinder;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.DatabaseConstraintMapValidator;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.DatabaseConstraintMapValidatorFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
 
public class MetadataConfigurations implements ApplicationContextAware {
    JdbcColumnMetadataRepository repository;

    private DatabaseConstraintMapValidator databaseConstraintMapValidator;
    private SpringBeanFinder beanFinder;

    private MetadataConfigurations(){

    }

    public DatabaseConstraintMapValidator getRepository(){
        return databaseConstraintMapValidator;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
         this.beanFinder = new SpringBeanFinder(applicationContext);
         databaseConstraintMapValidator = getDatabaseConstraintMapValidator();
    }

    private DatabaseConstraintMapValidator getDatabaseConstraintMapValidator(){
        DatabaseConstraintMapValidatorFactory databaseConstraintMapValidatorFactory = new DatabaseConstraintMapValidatorFactory(beanFinder);
        return databaseConstraintMapValidatorFactory.build();
    }

}

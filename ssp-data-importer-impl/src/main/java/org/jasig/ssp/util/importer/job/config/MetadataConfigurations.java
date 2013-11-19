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

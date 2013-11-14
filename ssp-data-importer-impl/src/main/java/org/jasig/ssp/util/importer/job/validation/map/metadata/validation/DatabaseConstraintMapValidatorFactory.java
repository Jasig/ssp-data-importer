package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import javax.validation.ValidatorFactory;

import org.jarbframework.constraint.metadata.database.ColumnMetadataRepository;
import org.jarbframework.utils.orm.SchemaMapper;
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

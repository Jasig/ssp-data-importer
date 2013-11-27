package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.utils.orm.ColumnReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.MapColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.TableColumnMetaDataRepository;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.TableMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.MissingPrimaryKeyViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates the property values of a bean satisfy our database constraints.
 * Performed database checks include:
 *
 * <ul>
 *  <li>Not nullable columns cannot have {@code null} values, unless they are generated</li>
 *  <li>Column length can never be exceeded. (char and number based)</li>
 *  <li>Fraction length in number column cannot be exceeded</li>
 * </ul>
 *
 * Whenever a database constraint is violated, a constraint violation will be
 * added for that property. Multiple constraint violations can occur, even on
 * the same property.
 *
 * @author James T Stanley
 * @since 23-05-2011
 */
public class DatabaseConstraintMapValidator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** Concrete validation logic that should be performed **/
    private final List<DatabaseConstraintMapValidationStep> validationSteps;

    /** Retrieves the column meta-data that we use for validation **/
    private TableColumnMetaDataRepository columnMetadataRepository;


    /**
     * Construct a new {@link DatabaseConstraintMapValidator}.
     */
    public DatabaseConstraintMapValidator() {
        validationSteps = new ArrayList<DatabaseConstraintMapValidationStep>();
        validationSteps.add(new NotNullConstraintMapValidationStep());
        validationSteps.add(new LengthConstraintMapValidationStep());
        validationSteps.add(new FractionLengthConstraintMapValidationStep());
    }

    /**
     * Determine if a bean follows all column constraints defined in the database.
     * @param bean the bean that should be validated
     * @param beanReference the reference to our bean, or {@code null} if the bean is our root
     * @param validatorContext used to create constraint violations
     * @return whether the bean is valid, or not
     */
    public boolean isValid(MapReference tableMapReference, ConstraintValidatorContext validatorContext) {
        DatabaseConstraintMapValidationContext validation = new DatabaseConstraintMapValidationContext();
        ((MapConstraintValidatorContext)validatorContext).setContext(validation);
        if(!validateKeys( tableMapReference, validation))
            return validation.isValid();
        validateMap(tableMapReference, validation);
        return validation.isValid();
    }

    private Boolean validateKeys(MapReference tableMapReference, DatabaseConstraintMapValidationContext validation){
            TableReference tableReference = new TableReference(tableMapReference.getTableName());
            TableMetadata tableMetadata = columnMetadataRepository.getTableMetadata(tableReference);
            if(!tableMetadata.hasKeys(tableMapReference.getTableMap())){
                validation.addViolation(new MissingPrimaryKeyViolation(tableMapReference,
                        tableMetadata.missingKeys(tableMapReference.getTableMap())));
                return false;
            }
           return true;
    }

    private void validateMap(MapReference tableMapReference, DatabaseConstraintMapValidationContext validation) {
        for (String columnName : tableMapReference.getTableMap().keySet()) {
            validateSimpleProperty(new MapReference(tableMapReference.getTableMap(),tableMapReference.getTableName(), columnName), tableMapReference, validation);
        }
    }

    private void validateSimpleProperty(MapReference mapReference, MapReference tableMapReference, DatabaseConstraintMapValidationContext validation) {
        //ColumnReference columnReference = schemaMapper.getColumnReference(tableMapReference); skip this for now assume column reference is available
        ColumnReference columnReference = new ColumnReference(mapReference.getTableName(), mapReference.getName());
        if (columnReference != null) {
            ColumnMetadata columnMetadata = columnMetadataRepository.getColumnMetadata(columnReference);
            if (columnMetadata != null) {
                String columnValue = tableMapReference.getTableMap().get(mapReference.getName());
                Object propertyValue = ((MapColumnMetadata)columnMetadata).convertValueToType(columnValue, mapReference, validation);
                for (DatabaseConstraintMapValidationStep validationStep : validationSteps) {
                    validationStep.validate(propertyValue, mapReference, columnMetadata, validation);
                }
            } else {
                logger.warn("Skipped validation because no metadata could be found for column '{}'.", columnReference);
            }
        }
    }


    public void setColumnMetadataRepository(TableColumnMetaDataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }

    public TableColumnMetaDataRepository getColumnMetadataRepository() {
        return this.columnMetadataRepository;
    }

}

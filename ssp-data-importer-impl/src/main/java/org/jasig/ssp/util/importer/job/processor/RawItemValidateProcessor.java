package org.jasig.ssp.util.importer.job.processor;


import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.csv.RawItemCsvWriter;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapConstraintValidatorContext;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.TableViolationException;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation.ViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;



public class RawItemValidateProcessor implements ItemProcessor<RawItem,RawItem> {

    private MetadataConfigurations metadataRepository;
    Logger logger = LoggerFactory.getLogger(RawItemCsvWriter.class);

    @Override
    public RawItem process(RawItem item) throws Exception {
        item.getRecord();
        String fileName = item.getResource().getFilename();
        String[] tableName = fileName.split("\\.");

        MapReference mapReference = new MapReference(item.getRecord(), tableName[0], null);
        MapConstraintValidatorContext validationContext = new  MapConstraintValidatorContext();
        Boolean isValid = metadataRepository.getRepository().isValid(mapReference, validationContext);
        if(isValid == false){
            if(validationContext.hasTableViolation())

                throw new TableViolationException(validationContext);
            else
                throw new ViolationException(validationContext);
        }
        return item;
    }

    public void setMetadataRepository(MetadataConfigurations metadataRepository){
        this.metadataRepository = metadataRepository;
    }

}

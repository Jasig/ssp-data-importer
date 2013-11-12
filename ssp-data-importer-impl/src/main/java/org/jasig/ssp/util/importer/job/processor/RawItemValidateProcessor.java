package org.jasig.ssp.util.importer.job.processor;


import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.validation.map.metadata.MapConstraintDescriptor;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.validation.MapConstraintValidatorContext;
import org.springframework.batch.item.ItemProcessor;



public class RawItemValidateProcessor implements ItemProcessor<RawItem,RawItem> {

    private MetadataConfigurations metadataRepository;

    @Override
    public RawItem process(RawItem item) throws Exception {
        item.getRecord();
        String fileName = item.getResource().getFilename();
        String[] tableName = fileName.split("\\.");
        MapReference mapReference = new MapReference(item.getRecord(), tableName[0], null);
        Boolean isValid = metadataRepository.getRepository().isValid(mapReference,
                new  MapConstraintValidatorContext( new MapConstraintDescriptor()));
        if(isValid == false){
            throw new Exception("bean not valid");
        }
        return item;
    }

    public void setMetadataRepository(MetadataConfigurations metadataRepository){
        this.metadataRepository = metadataRepository;
    }

}

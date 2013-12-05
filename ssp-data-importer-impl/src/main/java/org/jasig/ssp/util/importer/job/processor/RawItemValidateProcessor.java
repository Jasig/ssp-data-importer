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

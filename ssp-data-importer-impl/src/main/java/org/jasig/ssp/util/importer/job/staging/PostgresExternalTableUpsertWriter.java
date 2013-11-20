package org.jasig.ssp.util.importer.job.staging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

public class PostgresExternalTableUpsertWriter implements ItemWriter<RawItem>, StepExecutionListener {

    private Resource currentResource;
    private String[] orderedHeaders = null;
    private MetadataConfigurations metadataRepository;
    private StepExecution stepExecution;

   
    @Autowired
    private DataSource dataSource;

    @Override
    public void write(List<? extends RawItem> items) throws Exception {
        
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<String> batchedStatements = new ArrayList<String>();
        
        String sp = items.get(0).getResource().getFilename();
        String[] tableName = sp.split("\\.");
        
        Object batchStart = stepExecution.getExecutionContext().get("batchStart");
        Object batchStop = stepExecution.getExecutionContext().get("batchStop");
        
        RawItem item = items.get(0);
        if ( currentResource == null ) {
            this.orderedHeaders = writeHeader(items.get(0));
            this.currentResource = items.get(0).getResource();
        }
            Resource itemResource = item.getResource();
            if ( !(this.currentResource.equals(itemResource)) ) {
                say();
                this.orderedHeaders = writeHeader(item);
                this.currentResource = itemResource;
            }
            StringBuilder updateSql = new StringBuilder();
            updateSql.append(" UPDATE "+tableName[0]+" AS target ");
            updateSql.append(" SET ");
            for ( String header : this.orderedHeaders ) {
                updateSql.append(header+"=source."+header+",");
            }
            updateSql.deleteCharAt(updateSql.lastIndexOf(","));
            updateSql.append(" FROM stg_"+tableName[0]+" AS source WHERE ");
            List<String> tableKeys = metadataRepository.getRepository().getColumnMetadataRepository().getTableMetadata(new TableReference(tableName[0])).getTableKeys();
            if(tableKeys.isEmpty())
            {
                metadataRepository.getRepository().getColumnMetadataRepository().getTableMetadata(new TableReference("stg_"+tableName[0])).getTableKeys();
            }
            for (String key : tableKeys) {
                updateSql.append(" target."+key+" = source."+key+" AND ");
            }
            updateSql.append(" source.batch_id >= "+batchStart+" and source.batch_id <= "+batchStop+";");
            batchedStatements.add(updateSql.toString());
            say(updateSql);
            
            
            StringBuilder insertSql = new StringBuilder();
            
            insertSql.append(" INSERT INTO "+tableName[0]);
            insertSql.append(" SELECT ");
            for ( String header : this.orderedHeaders ) {
                insertSql.append(" source."+header).append(",");
            }           
            insertSql.setLength(insertSql.length() - 1); // trim comma
            insertSql.append(" FROM stg_"+tableName[0]+" AS source ");
            insertSql.append(" LEFT OUTER JOIN "+tableName[0]+" AS target ON ");
            for (String key : tableKeys) {
                insertSql.append(" source."+key+" = target."+key);
            }
            insertSql.append(" WHERE ");
            for (String key : tableKeys) {
                insertSql.append(" target."+key+" IS NULL AND ");
            }
            insertSql.append(" source.batch_id >= "+batchStart+" and source.batch_id <= "+batchStop+"");
            
            batchedStatements.add(insertSql.toString());
            say(insertSql);
        //jdbcTemplate.batchUpdate(batchedStatements.toArray(new String[]{}));
        say("******UPSERT******"+" batch start:"+batchStart+" batchstop:"+ batchStop);
    }


    private String[] writeHeader(RawItem item) {
        Map<String,String> firstRecord = item.getRecord();
        StringBuilder sb = new StringBuilder();
        List<String> headerColumns = new ArrayList<String>();
        for ( String key : firstRecord.keySet() ) {
            sb.append(key).append(",");
            headerColumns.add(key);
        }
        sb.setLength(sb.length() - 1); // trim comma
        return headerColumns.toArray(new String[headerColumns.size()]);
    }

    private void say(Object message) {
        System.out.println(message);
    }

    private void say() {
        say("");
    }

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

    public MetadataConfigurations getMetadataRepository() {
        return metadataRepository;
    }

    public void setMetadataRepository(MetadataConfigurations metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public StepExecution getStepExecution() {
        return stepExecution;
    }

    public void setStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
    
    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution arg0) {
       return  ExitStatus.COMPLETED;
    }

    @Override
    public void beforeStep(StepExecution arg0) {
        this.stepExecution = arg0;
    }    
    
}

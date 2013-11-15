package org.jasig.ssp.util.importer.job.staging;

import org.jarbframework.utils.orm.ColumnReference;
import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.MapColumnMetadata;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public class PostgresStagingTableWriter implements ItemWriter<RawItem> {

    private String preamble;
    private Resource currentResource;
    private String[] orderedHeaders = null;
    private MetadataConfigurations metadataRepository;
   
    @Autowired
    private DataSource dataSource;

    @Override
    public void write(List<? extends RawItem> items) throws Exception {
        
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<String> batchedStatements = new ArrayList<String>();
    	
        if ( items == null ) {
            this.currentResource = null;
            this.orderedHeaders = null;
            return;
        }
        
        
        if ( currentResource == null ) {
            this.orderedHeaders = writeHeader(items.get(0));
            this.currentResource = items.get(0).getResource();
        }
        String fileName = items.get(0).getResource().getFilename();
        String[] tableName = fileName.split("\\.");
        
        for ( RawItem item : items ) {
            Resource itemResource = item.getResource();
            if ( !(this.currentResource.equals(itemResource)) ) {
            	say();
                this.orderedHeaders = writeHeader(item);
                this.currentResource = itemResource;
            }
            StringBuilder insertSql = new StringBuilder();
            insertSql.append("INSERT INTO stg_"+tableName[0]+" (");
            
            StringBuilder valuesSqlBuilder = new StringBuilder();
            valuesSqlBuilder.append(" VALUES ( ");
            final Map<String,String> record = item.getRecord();
            for ( String header : this.orderedHeaders ) {
                String value;
                Integer sqlType = metadataRepository.getRepository().getColumnMetadataRepository().getColumnMetadata(new ColumnReference(tableName[0], header)).getJavaSqlType();
                if(isQuotedType(sqlType))
                {
                    value = "'" +  record.get(header) + "'";
                }
                else
                {
                    value =record.get(header);
                }
                insertSql.append(header).append(",");
                valuesSqlBuilder.append(value).append(",");
            }
            insertSql.setLength(insertSql.length() - 1); // trim comma
            valuesSqlBuilder.setLength(valuesSqlBuilder.length() - 1); // trim comma
            insertSql.append(")");
            valuesSqlBuilder.append(");");
            insertSql.append(valuesSqlBuilder);
            batchedStatements.add(insertSql.toString());
            say(insertSql);
        }
        jdbcTemplate.batchUpdate(batchedStatements.toArray(new String[]{}));
        say("******CHUNK POSTGRES******");
    }

    private boolean isQuotedType(Integer sqlType) {
        return Types.CHAR == sqlType || Types.DATE == sqlType || Types.LONGNVARCHAR == sqlType || Types.LONGVARCHAR == sqlType || Types.NCHAR == sqlType || Types.NVARCHAR == sqlType
                || Types.TIME == sqlType   || Types.TIMESTAMP== sqlType    || Types.VARCHAR == sqlType;
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

    public String getPreamble() {
        return preamble;
    }

    public void setPreamble(String preamble) {
        this.preamble = preamble;
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
}

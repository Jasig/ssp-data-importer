package org.jasig.ssp.util.importer.job.listener;

import java.sql.SQLException;
import java.util.Set;

import javax.sql.DataSource;

import org.jasig.ssp.util.importer.job.config.MetadataConfigurations;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.CachingTableColumnMetadataRepository;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;

public class StagingTableTruncator implements StepExecutionListener {

    private DataSource dataSource;

    private MetadataConfigurations metadataRepository;

    @Override
    public ExitStatus afterStep(StepExecution arg0) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            dataSource.getConnection().setAutoCommit(true);
            Set<TableReference> tables = ((CachingTableColumnMetadataRepository) metadataRepository.getRepository().getColumnMetadataRepository()).getColumnMetadataCache().getTableMetadataMap().keySet();
            for (TableReference tableReference : tables) {
                String sql = "truncate table stg_"+tableReference.getTableName()+";";
                jdbcTemplate.execute(sql);
                System.out.println(sql);
            }
        } catch (SQLException e) {
            System.out.println(e.getStackTrace());
        }
        System.out.println("DONE TRUNCATE");
        return ExitStatus.COMPLETED;
    }

    @Override
    public void beforeStep(StepExecution arg0) {


    }

    public MetadataConfigurations getMetadataRepository() {
        return metadataRepository;
    }

    public void setMetadataRepository(MetadataConfigurations metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }




}

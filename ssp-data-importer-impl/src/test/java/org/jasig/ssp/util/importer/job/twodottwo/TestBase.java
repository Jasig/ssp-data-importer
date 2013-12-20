package org.jasig.ssp.util.importer.job.twodottwo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class TestBase {

    private List<String> testTables = new ArrayList<String>();

    @Autowired
    private DataSource dataSource;

    public TestBase() {

    }

    public void cleanup() throws Exception{
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            dataSource.getConnection().setAutoCommit(true);
            ResultSet tablestg = dataSource.getConnection().getMetaData().getTables(null, null, "stg_%", new String[]{"TABLE"});
            ResultSet tables = dataSource.getConnection().getMetaData().getTables(null, null, "external_%", new String[]{"TABLE"});
            while(tables.next())
            {
                testTables.add(tables.getString("table_name"));
            }
            while(tablestg.next())
            {
                testTables.add(tablestg.getString("table_name"));
            }
            for (String table : testTables) {
                    String sql = "truncate table "+table+";";
                    jdbcTemplate.execute(sql);
            }
    }

    void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }
}

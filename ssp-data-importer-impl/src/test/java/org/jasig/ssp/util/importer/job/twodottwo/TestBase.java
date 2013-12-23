package org.jasig.ssp.util.importer.job.twodottwo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;

public class TestBase {

    @Autowired
    private DataSource dataSource;

    private Connection connection = null;

    private PreparedStatement preparedStatement = null;

    private ResultSet resultSet = null;

    public TestBase() {

    }

    private List<String> getTables() throws SQLException {
        Connection conn = getConnection();
        List<String> tables = new ArrayList<String>();
        ResultSet stgTables = conn.getMetaData().getTables(null, null, "stg_%",
                new String[] { "TABLE" });
        while (stgTables.next()) {
            tables.add(stgTables.getString("table_name"));
        }
        ResultSet externaTables = conn.getMetaData().getTables(null, null,
                "external_%", new String[] { "TABLE" });
        while (externaTables.next()) {
            tables.add(externaTables.getString("table_name"));
        }
        return tables;
    }

    public void cleanup() throws Exception {
        try {
            List<String> tables = getTables();
            for (String table : tables) {
                String sql = "truncate table " + table + ";";
                getExecuteStatement(sql);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            cleanConnections();
        }
    }

    private void cleanConnections() throws Exception {
        try {
            /**
             * Close the resultSet
             */
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            /**
             * Close the preparedStatement
             */
            if (preparedStatement != null) {
                preparedStatement.close();
                preparedStatement = null;
            }
            /**
             * Close the connection
             */
            if (connection != null) {
                connection.commit();
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public void runSQL(String sql) throws Exception {
        try {
            getExecuteStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cleanConnections();
        }
    }

    public Boolean validateMap(String sql, Map<String, Object> map,
            Integer count) throws Exception {
        Integer size = 0;
        ResultSet rowSet;
        try {
            rowSet = getExecuteQueryStatement(sql);

            while (rowSet.next()) {
                size++;
                for (String key : map.keySet()) {
                    Object value = map.get(key);
                    if (value.getClass().equals(String.class)) {
                        Assert.assertEquals(value, rowSet.getString(key));
                    }
                    if (value.getClass().equals(Long.class)) {
                        Long l = new Long(rowSet.getLong(key));
                        Assert.assertEquals(value, l);
                    }
                    if (value.getClass().equals(Integer.class)) {
                        Integer i = new Integer(rowSet.getInt(key));
                        Assert.assertEquals(value, i);
                    }
                    if (value.getClass().equals(BigDecimal.class)) {
                        BigDecimal dec = rowSet.getBigDecimal(key);
                        Assert.assertEquals(value, dec);
                    }
                    if (value.getClass().equals(Date.class)) {
                        Date date = rowSet.getDate(key);
                        Assert.assertEquals(value, date);
                    }
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            cleanConnections();
        }
        Assert.assertEquals(count, size);
        return true;
    }

    Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
        }

        return connection;
    }

    ResultSet getExecuteQueryStatement(String query) throws SQLException {
        preparedStatement = getConnection().prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    void getExecuteStatement(String query) throws SQLException {
        preparedStatement = getConnection().prepareStatement(query);
        preparedStatement.execute();
    }

    void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}

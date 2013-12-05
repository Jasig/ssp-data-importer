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
package org.jasig.ssp.util.importer.job.validation.map.metadata.database;

import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.StringUtils.upperCase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jasig.ssp.util.importer.job.validation.map.metadata.database.TableMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.MapColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;
import org.jarbframework.utils.JdbcConnectionCallback;
import org.jarbframework.utils.JdbcUtils;
import org.jarbframework.utils.orm.ColumnReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTableColumnMetadataRepository implements
        TableColumnMetaDataRepository {

     private final Logger logger = LoggerFactory.getLogger(JdbcTableColumnMetadataRepository.class);

        private final DataSource dataSource;

        private DatabaseIdentifierCaser identifierCaser;

        private String catalog;

        private String schema;

        public JdbcTableColumnMetadataRepository(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public MapColumnMetadata getColumnMetadata(final ColumnReference columnReference) {
            return JdbcUtils.doWithConnection(dataSource, new JdbcConnectionCallback<MapColumnMetadata>() {

                @Override
                public MapColumnMetadata doWork(Connection connection) throws SQLException {
                    DatabaseMetaData databaseMetaData = connection.getMetaData();
                    if (identifierCaser == null) {
                        identifierCaser = new DatabaseIdentifierCaser(databaseMetaData);
                    }

                    String tableName = identifierCaser.apply(columnReference.getTableName());
                    String columnName = identifierCaser.apply(columnReference.getColumnName());

                    logger.debug("Querying column metadata for table: {}, column: {}.", tableName, columnName);
                    ResultSet resultSet = databaseMetaData.getColumns(catalog, schema, tableName, columnName);

                    return mapToColumnMetadata(columnReference, resultSet);
                }

            });
        }

        /**
         * Convert a row inside the result set to column meta data.
         * @param columnReference the original column reference used during querying
         * @param resultSet the result set containing our information
         * @throws SQLException if any exception occurs
         */
        private MapColumnMetadata mapToColumnMetadata(ColumnReference columnReference, ResultSet resultSet) throws SQLException {
            MapColumnMetadata columnMetadata = null;
            if (resultSet.next()) {
                columnMetadata = new MapColumnMetadata(columnReference);
                columnMetadata.setDefaultValue(resultSet.getString("COLUMN_DEF"));

                Integer columnSize = getValueAsInteger(resultSet, "COLUMN_SIZE");

                if (columnSize != null && columnSize > 0) {
                    columnMetadata.setMaximumLength(columnSize);
                }

                columnMetadata.setJavaSqlType(getValueAsInteger(resultSet, "DATA_TYPE"));

                Integer fractionLength = getValueAsInteger(resultSet, "DECIMAL_DIGITS");
                if (fractionLength != null) {
                    columnMetadata.setFractionLength(Math.max(fractionLength, 0));
                }

                columnMetadata.setRadix(getValueAsInteger(resultSet, "NUM_PREC_RADIX"));
                columnMetadata.setRequired("NO".equals(getOptionalValue(resultSet, "IS_NULLABLE")));
                columnMetadata.setAutoIncrement("YES".equals(getOptionalValue(resultSet, "IS_AUTOINCREMENT")));
            }
            return columnMetadata;
        }

        private Integer getValueAsInteger(ResultSet resultSet, String columnLabel) throws SQLException {
            Integer value = null;
            String numberAsString = resultSet.getString(columnLabel);
            if (isNotBlank(numberAsString)) {
                value = Integer.parseInt(numberAsString);
            }
            return value;
        }



        private Object getOptionalValue(ResultSet resultSet, String columnLabel) {
            Object value = null;
            try {
                value = resultSet.getObject(columnLabel);
            } catch (SQLException e) {
                logger.trace("Column '" + columnLabel + "'  value could not be extracted from result set", e);
            }
            return value;
        }

        public void setCatalog(String catalog) {
            this.catalog = catalog;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        private static class DatabaseIdentifierCaser {

            private final String quoteString;

            private final boolean storeUpperCase;
            private final boolean storeUpperCaseQuoted;

            private final boolean storeLowerCase;
            private final boolean storeLowerCaseQuoted;

            public DatabaseIdentifierCaser(DatabaseMetaData databaseMetaData) throws SQLException {
                quoteString = databaseMetaData.getIdentifierQuoteString();
                storeUpperCase = databaseMetaData.storesUpperCaseIdentifiers();
                storeUpperCaseQuoted = databaseMetaData.storesUpperCaseQuotedIdentifiers();
                storeLowerCase = databaseMetaData.storesLowerCaseIdentifiers();
                storeLowerCaseQuoted = databaseMetaData.storesLowerCaseQuotedIdentifiers();
            }

            public String apply(String identifier) {
                if (isQuoted(identifier)) {
                    identifier = applyQuoted(identifier);
                } else {
                    if (storeLowerCase) {
                        identifier = lowerCase(identifier);
                    } else if (storeUpperCase) {
                        identifier = upperCase(identifier);
                    }
                }
                return identifier;
            }

            private boolean isQuoted(String identifier) {
                boolean quoted = false;
                if (isNotBlank(quoteString)) {
                    quoted = startsWith(identifier, quoteString) && endsWith(identifier, quoteString);
                }
                return quoted;
            }

            private String applyQuoted(String identifier) {
                String unquotedIdentifier = substringBetween(identifier, quoteString);
                if (storeLowerCaseQuoted) {
                    unquotedIdentifier = lowerCase(unquotedIdentifier);
                } else if (storeUpperCaseQuoted) {
                    unquotedIdentifier = upperCase(unquotedIdentifier);
                }
                return unquotedIdentifier;
            }

        }

        @Override
        public TableMetadata getTableMetadata(final TableReference tableReference) {
            return JdbcUtils.doWithConnection(dataSource, new JdbcConnectionCallback<TableMetadata>() {

                @Override
                public TableMetadata doWork(Connection connection) throws SQLException {
                    DatabaseMetaData databaseMetaData = connection.getMetaData();
                    if (identifierCaser == null) {
                        identifierCaser = new DatabaseIdentifierCaser(databaseMetaData);
                    }

                    String tableName = identifierCaser.apply(tableReference.getTableName());

                    logger.debug("Querying table metadata for table: {}.", tableName);
                    ResultSet resultSet = databaseMetaData.getPrimaryKeys(catalog, schema, tableName);

                    return mapToTableMetadata(tableReference, resultSet);
                }

            });
        }

        /**
         * Convert a row inside the result set to table meta data.
         * @param tableReference the original table reference used during querying
         * @param resultSet the result set containing our information
         * @throws SQLException if any exception occurs
         */
        private TableMetadata mapToTableMetadata(TableReference tableReference, ResultSet resultSet) throws SQLException {
            TableMetadata tableMetadata = new TableMetadata(tableReference);
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while(resultSet.next()) {
                tableMetadata.addKey(resultSet.getString("COLUMN_NAME"));
            }
            resultSet.close();
            return tableMetadata;
        }


}

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

import java.util.HashMap;
import java.util.Map;

import org.jasig.ssp.util.importer.job.validation.map.metadata.database.TableMetadata;
import org.jarbframework.utils.orm.ColumnReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;

public class MapTableColumnMetadataRepository implements
        TableColumnMetaDataRepository {

    /** Map containing all column metadata. **/
    private final Map<ColumnReference, MapColumnMetadata> columnMetadataMap;

    /** Map containing all column metadata. **/
    private final Map<TableReference, TableMetadata> tableMetadataMap;

    /**
     * Construct a new {@link MapColumnMetadataRepository}.
     */
    public MapTableColumnMetadataRepository() {
        columnMetadataMap = new HashMap<ColumnReference, MapColumnMetadata>();
        tableMetadataMap = new HashMap<TableReference, TableMetadata>();
    }

    /**
     * Store a column constraint inside this repository.
     * @param columnMetadata column constraint that should be added
     * @return the same repository instance, for chaining
     */
    public MapTableColumnMetadataRepository addColumnMetadata(MapColumnMetadata columnMetadata) {
        getColumnMetadataMap().put(columnMetadata.getColumnReference(), columnMetadata);
        return this;
    }

    /**
     * Store a column constraint inside this repository.
     * @param columnMetadata column constraint that should be added
     * @return the same repository instance, for chaining
     */
    public MapTableColumnMetadataRepository addTableMetadata(TableMetadata tableMetadata) {
        getTableMetadataMap().put(tableMetadata.getTableReference(), tableMetadata);
        return this;
    }

    /**
     * Remove all stored constraints from this repository.
     * @return the same repository instance, for chaining
     */
    public MapTableColumnMetadataRepository removeAll() {
        getColumnMetadataMap().clear();
        getTableMetadataMap().clear();
        return this;
    }

    @Override
    public MapColumnMetadata getColumnMetadata(ColumnReference columnReference) {
        return getColumnMetadataMap().get(columnReference);
    }

    @Override
    public TableMetadata getTableMetadata(TableReference tableReference) {
        return getTableMetadataMap().get(tableReference);
    }
    
    public Map<ColumnReference, MapColumnMetadata> getColumnMetadataMap() {
        return columnMetadataMap;
    }
    public Map<TableReference, TableMetadata> getTableMetadataMap() {
        return tableMetadataMap;
    }

}

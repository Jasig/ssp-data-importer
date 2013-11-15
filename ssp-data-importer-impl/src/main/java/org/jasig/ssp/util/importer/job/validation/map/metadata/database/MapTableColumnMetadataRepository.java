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

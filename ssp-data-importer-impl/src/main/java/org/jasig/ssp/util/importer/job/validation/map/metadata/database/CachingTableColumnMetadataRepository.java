package org.jasig.ssp.util.importer.job.validation.map.metadata.database;

import org.jasig.ssp.util.importer.job.validation.map.metadata.database.TableColumnMetaDataRepository;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.MapTableColumnMetadataRepository;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.TableMetadata;
import org.jarbframework.utils.Asserts;
import org.jarbframework.utils.orm.ColumnReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;

public class CachingTableColumnMetadataRepository implements
        TableColumnMetaDataRepository {
     /** Maintains all constraint meta-data in memory **/
    private final MapTableColumnMetadataRepository columnMetadataCache = new MapTableColumnMetadataRepository();
 

    /** Capable of filling our cache with constraint meta-data **/
    private final TableColumnMetaDataRepository columnMetadataRepository;

    /**
     * Construct a new {@link CachingColumnMetadataRepository}.
     * @param columnMetadataRepository provides column meta-data for our cache
     */
    public CachingTableColumnMetadataRepository(TableColumnMetaDataRepository columnMetadataRepository) {
        this.columnMetadataRepository = Asserts.notNull(columnMetadataRepository, "Delegate column metadata repository cannot be null.");
    }

    @Override
    public MapColumnMetadata getColumnMetadata(ColumnReference columnReference) {
        MapColumnMetadata columnMetadata = getColumnMetadataCache().getColumnMetadata(columnReference);
        if (columnMetadata == null) {
            columnMetadata = lookupAndCacheColumnMetadata(columnReference);
        }
        return columnMetadata;
    }

    @Override
    public TableMetadata getTableMetadata(TableReference tableReference) {
        TableMetadata tableMetadata = getColumnMetadataCache().getTableMetadata(tableReference);
        if (tableMetadata == null) {
            tableMetadata = lookupAndCacheTableMetadata(tableReference);
        }
        return tableMetadata;
    }

    private MapColumnMetadata lookupAndCacheColumnMetadata(ColumnReference columnReference) {
        MapColumnMetadata columnMetadata = columnMetadataRepository.getColumnMetadata(columnReference);
        if (columnMetadata != null) {
            getColumnMetadataCache().addColumnMetadata(columnMetadata);
        }
        return columnMetadata;
    }

    private TableMetadata lookupAndCacheTableMetadata(TableReference tableReference) {
        TableMetadata tableMetadata = columnMetadataRepository.getTableMetadata(tableReference);
        if (tableMetadata != null) {
            getColumnMetadataCache().addTableMetadata(tableMetadata);
        }
        return tableMetadata;
    }

    /**
     * Remove all cached metdata. Clearing the cache might be desirable
     * whenever the table structure has changed during runtime.
     */
    public void clearCache() {
        getColumnMetadataCache().removeAll();
    }

    public MapTableColumnMetadataRepository getColumnMetadataCache() {
        return columnMetadataCache;
    }


}

package org.jasig.ssp.util.importer.job.validation.map.metadata.database;

import org.jarbframework.utils.orm.ColumnReference;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.TableMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.database.MapColumnMetadata;
import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;

public interface TableColumnMetaDataRepository {

    TableMetadata getTableMetadata(TableReference tableReference);
    MapColumnMetadata getColumnMetadata(ColumnReference columnReference);
}

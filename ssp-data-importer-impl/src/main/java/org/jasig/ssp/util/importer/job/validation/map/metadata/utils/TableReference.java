package org.jasig.ssp.util.importer.job.validation.map.metadata.utils;

import static org.jarbframework.utils.Asserts.hasText;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TableReference {

    private String tableName;
    /**
     * Create a new database column reference.
     * @param tableName name of the table
     * @param columnName name of the column
     */
    public TableReference(String tableName) {
        this.tableName = hasText(tableName, "Table name is required");
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

package org.jasig.ssp.util.importer.job.validation.map.metadata.utils;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.jarbframework.utils.Asserts.hasText;
import static org.jarbframework.utils.Asserts.notNull;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MapReference {
        private static final String PROPERTY_SEPARATOR = ".";

        private final String name;

        private final String tableName;

        private final Map<String, String> tableMap;

        public MapReference(Map<String,String> tableMap, String tableName, String name) {
            this.name = name;
            this.tableName = hasText(tableName, "Property name is required");
            this.tableMap = notNull(tableMap, "Table Map is required");
        }

        public MapReference(MapReference parent, String name) {
            this(parent.getTableMap(), parent.getTableName(), parent.getName() + PROPERTY_SEPARATOR + name);
        }

        public String getName() {
            return name;
        }

        public String getSimpleName() {
            return isNestedProperty() ? substringAfterLast(name, PROPERTY_SEPARATOR) : name;
        }

        public Map<String,String> getTableMap() {
            return tableMap;
        }

        public String getTableName() {
            return tableName;
        }

        public boolean isNestedProperty() {
            return name.contains(PROPERTY_SEPARATOR);
        }

        public String getNestedName() {
            return isNestedProperty() ? substringAfter(name, PROPERTY_SEPARATOR) : name;
        }


        public String[] getPath() {
            return StringUtils.split(name, PROPERTY_SEPARATOR);
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
            return tableName + "." + name;
        }

}

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
package org.jasig.ssp.util.importer.job.validation.map.metadata.utils;

import static org.jarbframework.utils.Asserts.hasText;
import static org.jarbframework.utils.Asserts.notNull;

import java.util.Map;

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


        public Map<String,String> getTableMap() {
            return tableMap;
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
            return tableName + "." + name;
        }

}

/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2021-2021 the original author or authors.
 */

package org.stdg.dbtype;

import org.stdg.*;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

class H2MetadataFinder implements DatabaseMetadataFinder {

    private static final SqlQuery H2_REFERENCED_TABLES_QUERY = new SqlQuery(
            "with\n" +
                    "    recursive parent_child_tree (table_name, ref_table_name, level) as\n" +
                    "    (\n" +
                    "    select distinct\n" +
                    "        child.table_name     as table_name,\n" +
                    "        parent.table_name    as ref_table_name,\n" +
                    "        1                    as level\n" +
                    "    from information_schema.table_constraints child\n" +
                    "    join information_schema.referential_constraints rco\n" +
                    "    on rco.constraint_name = child.constraint_name\n" +
                    "    join information_schema.constraints parent\n" +
                    "    on parent.unique_index_name = rco.unique_constraint_name\n" +
                    "    where\n" +
                    "        child.table_name != parent.table_name and\n" +
                    "        child.table_name=?\n" +
                    "    UNION ALL\n" +
                    "    select pc.table_name, pc.ref_table_name, pct.level + 1 as level\n" +
                    "    from\n" +
                    "        (\n" +
                    "        select \n" +
                    "            child.table_name     as table_name,\n" +
                    "            parent.table_name    as ref_table_name,\n" +
                    "            1                    as level\n" +
                    "        from information_schema.table_constraints child\n" +
                    "        join information_schema.referential_constraints rco\n" +
                    "        on rco.constraint_name = child.constraint_name\n" +
                    "        join information_schema.constraints parent\n" +
                    "        on parent.unique_index_name = rco.unique_constraint_name\n" +
                    "        where\n" +
                    "            child.table_name != parent.table_name\n" +
                    "        ) pc\n" +
                    "    join parent_child_tree pct on (pc.table_name = pct.ref_table_name)\n" +
                    "    )\n" +
                    "select distinct *\n" +
                    "from parent_child_tree\n" +
                    "order by level desc");

    private static final SqlQuery H2_COLUMNS_MAPPINGS_QUERY = new SqlQuery(
            "select \n" +
                    "        fktable_schema as table_schema,\n" +
                    "        fktable_name   as table_name,\n" +
                    "        fkcolumn_name  as column_name,\n" +
                    "        pktable_schema as ref_table_schema,\n" +
                    "        pktable_name   as ref_table_name,\n" +
                    "        pkcolumn_name  as ref_column_name\n" +
                    "  from information_schema.cross_references \n" +
                    "  where fktable_name = ?"
    );

    private final DefaultColumnOrdersFinder defaultColumnOrdersFinder;

    private final NotNullColumnsFinder defaultNotNullColumnsFinder;

    private final ReferencedTablesFinder h2ReferencedTablesFinder;

    private final ColumnsMappingsFinder h2ColumnsMappingsFinder;

    private final PrimaryKeyColumnsFinder primaryKeyColumnsFinder;

    H2MetadataFinder(DataSource dataSource) {
        this.defaultColumnOrdersFinder = new DefaultColumnOrdersFinder(dataSource);
        this.defaultNotNullColumnsFinder = new DefaultNotNullColumnsFinder(dataSource);
        this.h2ReferencedTablesFinder = new BaseReferencedTablesFinder(dataSource, H2_REFERENCED_TABLES_QUERY);
        this.h2ColumnsMappingsFinder = new BaseColumnsMappingsFinder(dataSource, H2_COLUMNS_MAPPINGS_QUERY);
        this.primaryKeyColumnsFinder = new DefaultPrimaryKeyColumnsFinder(dataSource);
    }

    @Override
    public List<String> findDatabaseColumnOrdersOf(String tableName) {
        return defaultColumnOrdersFinder.findDatabaseColumnOrdersOf(tableName);
    }

    @Override
    public Collection<String> findNotNullColumnsOf(String tableName) {
        return defaultNotNullColumnsFinder.findNotNullColumnsOf(tableName);
    }

    @Override
    public ReferencedTableSet findReferencedTablesOf(String tableName) {
        return h2ReferencedTablesFinder.findReferencedTablesOf(tableName);
    }

    @Override
    public ColumnsMappingGroup findColumnsMappingsOf(String tableName) {
        return h2ColumnsMappingsFinder.findColumnsMappingsOf(tableName);
    }

    @Override
    public List<String> findPrimaryColumnsOf(String tableName) {
        return primaryKeyColumnsFinder.findPrimaryColumnsOf(tableName);
    }

}

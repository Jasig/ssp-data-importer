package org.jasig.ssp.util.importer.job.validation.map.metadata.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference;

public class TableMetadata {
    private List<String> tableKeys = new ArrayList<String>();

    private final TableReference tableReference;


    public TableMetadata(org.jasig.ssp.util.importer.job.validation.map.metadata.utils.TableReference tableReference) {
        super();
        this.tableReference = tableReference;
    }

    public List<String> getTableKeys() {
        return tableKeys;
    }

    public void addKey(String tableKey) {
        this.tableKeys.add(tableKey);
    }

    public Boolean hasKeys(Map<String,String> tableMap){
        for(String tableKey: tableKeys){
            if(!tableMap.containsKey(tableKey))
                return false;
        }
        return true;
    }

    public TableReference getTableReference(){
        return tableReference;
    }
}

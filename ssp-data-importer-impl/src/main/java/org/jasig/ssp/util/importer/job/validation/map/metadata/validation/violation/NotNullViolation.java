/**
 *
 */
package org.jasig.ssp.util.importer.job.validation.map.metadata.validation.violation;

import org.jasig.ssp.util.importer.job.validation.map.metadata.utils.MapReference;

/**
 * @author jamesstanley
 *
 */
public class NotNullViolation extends GenericMapViolation {


    public NotNullViolation(MapReference mapReference, Object columnValue){
        super(mapReference, columnValue, "Not Null violation");
    }


}

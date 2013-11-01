package org.jasig.ssp.util.dataimport.job.processor;


import org.jasig.ssp.util.dataimport.job.domain.RawItem;
import org.springframework.batch.item.ItemProcessor;

public class RawItemToBeanProcessor implements ItemProcessor<RawItem,Object> {

    @Override
    public Object process(RawItem item) throws Exception {
        // TODO Port Groovy impl for mapping CSV records (as maps) to SSP entity beans. Whether or not this
        // also includes the validation step of those beans is TBD
        throw new UnsupportedOperationException("Implement me");
    }
}

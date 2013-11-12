package org.jasig.ssp.util.importer.job.processor;


import org.jasig.ssp.util.importer.job.domain.RawItem;

import org.springframework.batch.item.ItemProcessor;


/**
 * Dummy {@link ItemProcessor} for prototype purposes that just echos the {@link RawItem} it's been given.
 *
 */
public class RawItemEchoProcessor implements ItemProcessor<RawItem,RawItem> {
    @Override
    public RawItem process(RawItem item) throws Exception {

        return item;
    }
}

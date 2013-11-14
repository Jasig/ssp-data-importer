package org.jasig.ssp.util.importer.job.listener;

import org.jasig.ssp.util.importer.job.domain.RawItem;
import org.springframework.batch.core.ItemProcessListener;

public class RawItemValidateProcessorListener implements ItemProcessListener<RawItem, RawItem> {

    @Override
    public void beforeProcess(RawItem item) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterProcess(RawItem item, RawItem result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProcessError(RawItem item, Exception e) {
        System.out.println(e.getMessage());
    }

}

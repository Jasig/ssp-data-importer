package org.jasig.ssp.util.importer.job;

import org.springframework.batch.core.launch.support.CommandLineJobRunner;

/**
 * To make it easy to bootstrap the job from within the IDEA but not necessarily in the scope of a test class.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        CommandLineJobRunner.main(args);
    }

}

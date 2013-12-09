package org.jasig.ssp.util.importer

import org.springframework.batch.item.ExecutionContext;
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.core.io.Resource
import spock.lang.Specification

import org.jasig.ssp.util.importer.job.csv.RawItemCsvReader
import org.jasig.ssp.util.importer.job.domain.RawItem
import org.springframework.batch.core.StepExecution;

class RawitemReaderTest extends Specification {

    private ConfigurableApplicationContext context
    private RawItemCsvReader rawItemReader
    private ExecutionContext executionContext


    String VALID_RESOURCE = "classpath:data-test/raw-item-csv-reader-test/valid-line-test.csv"

    def setup() {
        System.setProperty("ssp.importer.configdir", "")
        context = new ClassPathXmlApplicationContext('classpath:config-context.xml','classpath:config-context-test.xml');
        rawItemReader = new RawItemCsvReader()
        executionContext = new ExecutionContext()
    }

    def cleanup() {

        System.clearProperty('ssp.importer.configdir')
        if ( context != null ) context.close()
        rawItemReader.close()
    }

    def "raw item reader parses valid line, default escape and delimiter"() {

        setup:
        RawItemCsvReader rawItemReader = new RawItemCsvReader()
        rawItemReader.saveStepExecution()

        when: "when a rawItemCsvReader is given a valid resource"
        Resource resource = context.getResource(VALID_RESOURCE);
        StepExecution stepExecution = Mock()
        stepExecution.getExecutionContext() >> new HashMap<String,String>()
        assert resource.getFile().exists() == true
        rawItemReader.setResource(resource)
        rawItemReader.saveStepExecution(stepExecution)
        rawItemReader.afterPropertiesSet()
        rawItemReader.open(executionContext)

        then: "resource is read and a map is created"

        RawItem rawItem = rawItemReader.read()
        Map<String,String> map = rawItem.getRecord();
        map.get("string").equals("this is a string.")
        map.get("string_escape").equals("this is a string \", with escaped quotes")
        map.get("string_quote").equals("this is a string in quotes")
        map.get("date_bday").equals("1983-08-20")
        map.get("date_term").equals("2012-04-30 00:00:00")
        map.get("timestamp").equals("899902111")
        map.get("time").equals("08:30:40")
        map.get("integer").equals("12345678")
        map.get("bigint").equals("99999999999")
        map.get("number").equals("9.3E10")
        map.get("char").equals("A")
        map.get("boolean").equals("TRUE")
    }

}

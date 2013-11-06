package org.jasig.ssp.util.importer

import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Specification

class PropertyPlaceholderTest extends Specification {

    ConfigurableApplicationContext context


    def cleanup() {
        System.clearProperty('ssp.importer.configdir')
        if ( context != null ) context.close()
    }

    def "external config override dir is not required"() {

        setup:
        assert System.getProperty('ssp.importer.configdir') == null

        when: "a Spring context is created without specifying an override dir"
        context = new ClassPathXmlApplicationContext('classpath:config-context.xml','classpath:config-context-test.xml');

        then: "the default config value is preserved"
        context.getBean('configReferences')['exists.only.for.testing.1'] == 'default.value.1'

    }

    def "external config override file is not required"() {

        given: "a config override dir with an unexpected override file name"
        def configOverrideResource = PropertyPlaceholderTest.class.classLoader.getResource("config-override-dir-misnamed-file/wrong-name.properties").toURI()
        def configOverrideDir = new File(configOverrideResource).parentFile
        System.setProperty("ssp.importer.configdir", configOverrideDir.absolutePath)

        when: "a Spring context is created using that override dir"
        def context = new ClassPathXmlApplicationContext('classpath:config-context.xml','classpath:config-context-test.xml');

        then: "the default config value is preserved"
        context.getBean('configReferences')['exists.only.for.testing.1'] == 'default.value.1'

    }

    def "external config override file preferred over defaults"() {

        given: "a dir with a file that overrides the value of 'exists.only.for.testing' config"
        def configOverrideResource = PropertyPlaceholderTest.class.classLoader.getResource("config-override-dir/ssp-importer.properties").toURI()
        def configOverrideDir = new File(configOverrideResource).parentFile
        System.setProperty("ssp.importer.configdir", configOverrideDir.absolutePath)

        when: "a Spring context is created using that override dir/file"
        def context = new ClassPathXmlApplicationContext('classpath:config-context.xml','classpath:config-context-test.xml');

        then: "the value in the override directory/file wins"
        context.getBean('configReferences')['exists.only.for.testing.1'] == 'override.value.1'

    }

    def "config properties can reference other properties"() {

        given: "a dir with a file that overrides default config"
        def configOverrideResource = PropertyPlaceholderTest.class.classLoader.getResource("config-override-dir-references/ssp-importer.properties").toURI()
        def configOverrideDir = new File(configOverrideResource).parentFile
        System.setProperty("ssp.importer.configdir", configOverrideDir.absolutePath)

        when: "a Spring context is created using that override dir/file"
        def context = new ClassPathXmlApplicationContext('classpath:config-context.xml','classpath:config-context-test.xml');

        then: "the value in the override directory/file wins"
        context.getBean('configReferences')['exists.only.for.testing.2'] == 'prefix-default.value.1'

    }
}

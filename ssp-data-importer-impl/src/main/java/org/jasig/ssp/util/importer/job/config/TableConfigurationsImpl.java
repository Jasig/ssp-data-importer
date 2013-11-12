package org.jasig.ssp.util.importer.job.config;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.core.io.FileSystemResource;

public class TableConfigurationsImpl implements TableConfigurations {

    private Map<String, TableConfiguration> tablesMap = new HashMap<String, TableConfiguration>();

    private static String configurationFileLocation;

    private static String jarBeanLocation;

    private TableConfigurationsImpl(String configurationFileLocation, String jarBeanLocation) throws JsonParseException, JsonMappingException, SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        initialize(configurationFileLocation, jarBeanLocation);
    }

    private void initialize(String configurationFileLocation, String jarBeanLocation) throws JsonParseException, JsonMappingException, IOException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
         addClassPath(jarBeanLocation);
         ObjectMapper mapper = new ObjectMapper();
         FileSystemResource resourceLocator = new FileSystemResource(configurationFileLocation);
         List<TableConfiguration> tables = mapper.readValue(resourceLocator.getFile(),
                 new TypeReference<List<TableConfiguration>>(){});

         for(TableConfiguration table:tables)
         {
             tablesMap.put(table.getFileName().toLowerCase(), table);
         }
    }

    @Override
    public TableConfiguration getConfiguration(String fileName) {
        return tablesMap.get(fileName.toLowerCase());
    }


    private void addClassPath(String jarBeanLocation) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException{
        FileSystemResource resourceLocator = new FileSystemResource(jarBeanLocation);
        URL u = resourceLocator.getURL();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[]{u});
    }

    public static String getConfigurationFileLocation() {
        return configurationFileLocation;
    }

    public static void setConfigurationFileLocation(String configurationFileLocation) {
        TableConfigurationsImpl.configurationFileLocation = configurationFileLocation;
    }

    public String getJarBeanLocation() {
        return jarBeanLocation;
    }

    public void setJarBeanLocation(String jarBeanLocation) {
        TableConfigurationsImpl.jarBeanLocation = jarBeanLocation;
    }

}

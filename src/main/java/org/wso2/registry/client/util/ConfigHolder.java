package org.wso2.registry.client.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHolder {

    private Properties properties = new Properties();

    private ConfigHolder() {
        init();
    }

    private static class LazyHolder {
        private static final ConfigHolder INSTANCE = new ConfigHolder();
    }

    public static ConfigHolder getInstance() {
        return LazyHolder.INSTANCE;
    }

    private void init() {

        InputStream input = null;
        try {
            input = new FileInputStream("resources/client.properties");
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Can't find/read 'resources/client.properties' file. Copy operation terminated.");
            e.printStackTrace();
            System.exit(-1);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}

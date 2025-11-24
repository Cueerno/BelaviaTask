package com.radiuk.belavia_task.config;

import com.radiuk.belavia_task.exception.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyConfig {

    private final Properties properties = new Properties();

    public PropertyConfig(String fileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            properties.load(is);
        } catch (IOException exception) {
            throw new ConfigurationException("Failed to load properties file: " + fileName, exception);
        }
    }

    public String getString(String key) {
        String v = properties.getProperty(key);
        if (v == null) {
            throw new ConfigurationException("Missing property: " + key);
        }
        return v;
    }

    public int getInt(String key) {
        String v = properties.getProperty(key);
        if (v == null) {
            throw new ConfigurationException("Missing integer property: " + key);
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Invalid integer for property " + key + ": " + v, e);
        }
    }
}

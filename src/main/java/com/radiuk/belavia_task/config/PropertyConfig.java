package com.radiuk.belavia_task.config;

import com.radiuk.belavia_task.exception.ConfigurationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyConfig {

    private final Properties properties = new Properties();

    public PropertyConfig(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException exception) {
            throw new ConfigurationException("Failed to load properties from " + filePath, exception);
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

package me.bannock.tailgater.configuration;

import java.io.IOException;

public interface ConfigurationManager {

    /**
     * Gets a value from the configuration
     * @param key The key to get the value for
     * @param clazz The class of the value, used for casting
     * @return The value
     * @param <T> The type of the value
     */
    <T> T getConfigurationValue(String key, Class<T> clazz);

    /**
     * Gets a value from the configuration
     * @param key The key to get the value for
     * @param clazz The class of the value, used for casting
     * @return The value
     * @param <T> The type of the value
     */
    default <T> T getConfigurationValue(Enum<?> key, Class<T> clazz){
        return getConfigurationValue(key.name(), clazz);
    }

    /**
     * Sets a value in the configuration
     * @param key The key to set the value for
     * @param value The value to set
     */
    void setConfigurationValue(String key, Object value);

    /**
     * Sets a value in the configuration
     * @param key The key to set the value for
     * @param value The value to set
     */
    default void setConfigurationValue(Enum<?> key, Object value){
        setConfigurationValue(key.name(), value);
    }

    /**
     * Sets a default value in the configuration, as well as the value type
     * @param key The key to set the default value for
     * @param defaultValue The default value to set
     * @return this instance
     */
    ConfigurationManager setConfigurationDefault(String key, Object defaultValue);

    /**
     * Sets a default value in the configuration, as well as the value type
     * @param key The key to set the default value for
     * @param defaultValue The default value to set
     * @return this instance
     */
    default ConfigurationManager setConfigurationDefault(Enum<?> key, Object defaultValue){
        return setConfigurationDefault(key.name(), defaultValue);
    }

    /**
     * Saves the configuration
     * @return true if the configuration was saved, otherwise false
     */
    boolean saveConfiguration();

    /**
     * Loads the configuration
     * @return this instance
     * @throws IOException if the configuration could not be loaded
     */
    ConfigurationManager loadConfiguration();

}

package me.bannock.tailgater.configuration;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PiJsonConfigurationManagerImpl implements ConfigurationManager {

    private final File configFile = new File("configuration.json");
    private final Map<String, Object> config = new HashMap<>();
    private final Map<String, Object> defaults = new HashMap<>();

    @Override
    public <T> T getConfigurationValue(String key, Class<T> clazz) {
        if (config.containsKey(key))
            return clazz.cast(config.get(key));
        else if (defaults.containsKey(key))
            return clazz.cast(defaults.get(key));
        else
            return null;
    }

    @Override
    public void setConfigurationValue(String key, Object value) {
        config.put(key, value);
    }

    @Override
    public ConfigurationManager setConfigurationDefault(String key, Object defaultValue) {
        defaults.put(key, defaultValue);
        return this;
    }

    @Override
    public boolean saveConfiguration() {
        try{
            FileUtils.writeStringToFile(configFile, new JSONObject(config).toString(4), StandardCharsets.UTF_8);
            return true;
        }catch (IOException e){
            return false;
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ConfigurationManager loadConfiguration() {
        // Create the configuration file if it doesn't exist
        if (!configFile.exists()){
            defaults.keySet().forEach(key -> config.put(key, defaults.get(key)));
            saveConfiguration();
            return this;
        }

        // Read configuration file
        try{
            JSONObject json = new JSONObject(FileUtils.readFileToString(configFile, StandardCharsets.UTF_8));
            for (String key : json.keySet()){

                // Handle in case the type of this value is known
                if (defaults.containsKey(key)){
                    Object defaultValue = defaults.get(key);
                    if (defaultValue instanceof Integer)
                        config.put(key, json.getInt(key));
                    else if (defaultValue instanceof Double)
                        config.put(key, json.getDouble(key));
                    else if (defaultValue instanceof Boolean)
                        config.put(key, json.getBoolean(key));
                    else if (defaultValue instanceof String)
                        config.put(key, json.getString(key));
                    else if (defaultValue instanceof Enum<?>)
                        config.put(key, json.getEnum((Class<? extends Enum>) defaultValue.getClass(), key));
                    else
                        config.put(key, new Gson().fromJson(json.getJSONObject(key).toString(), defaultValue.getClass()));
                }

                // Handle in case the type of this value is unknown
                else {
                    config.put(key, json.get(key));
                }
            }
        }catch (IOException e){
            throw new RuntimeException("Could not read configuration file", e);
        }
        return this;
    }
}

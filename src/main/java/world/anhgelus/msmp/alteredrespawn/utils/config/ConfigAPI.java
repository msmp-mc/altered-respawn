package world.anhgelus.msmp.alteredrespawn.utils.config;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * @author Robotv2
 */
public class ConfigAPI {

    private static Plugin plugin;
    private static final HashMap<String, Config> configs = new HashMap<>();

    /**
     * Get the configuration
     * @param name Name of the configuration
     * @return The configuration
     */
    public static Config getConfig(String name) {
        if(plugin == null) {
            throw new NullPointerException("plugin");
        }
        Config config = configs.get(name);
        if(config == null) {
            config = new Config(ConfigAPI.plugin, name);
            configs.put(name, config);
        }
        return config;
    }

    /**
     * Init the ConfigAPI
     * @param plugin Plugin's main file
     */
    public static void init(Plugin plugin) {
        ConfigAPI.plugin = plugin;
    }
}
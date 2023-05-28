package world.anhgelus.msmp.alteredrespawn

import org.bukkit.plugin.java.JavaPlugin
import world.anhgelus.msmp.alteredrespawn.utils.config.ConfigAPI
import java.util.logging.Logger

class AlteredRespawn: JavaPlugin() {
    override fun onEnable() {
        // init vars
        LOGGER = logger
        ConfigAPI.init(this)

        LOGGER.info("AlteredRespawn has been enabled!")
    }

    override fun onDisable() {
        LOGGER.info("AlteredRespawn has been disabled!")
    }

    companion object {
        lateinit var INSTANCE: AlteredRespawn
            private set
        lateinit var LOGGER: Logger

        fun getInstance(): AlteredRespawn {
            return INSTANCE
        }
    }
}
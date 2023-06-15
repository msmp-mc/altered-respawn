package world.anhgelus.msmp.alteredrespawn

import org.bukkit.Bukkit
import world.anhgelus.msmp.alteredrespawn.api.Stack
import world.anhgelus.msmp.alteredrespawn.listener.RegisterListener
import world.anhgelus.msmp.msmpcore.PluginBase
import world.anhgelus.msmp.msmpcore.utils.config.ConfigHelper

class AlteredRespawn: PluginBase() {
    object ConfigAPI : ConfigHelper()

    override val configHelper = ConfigAPI
    override val pluginName: String = "AlteredRespawn"

    override fun enable() {
        INSTANCE = this
        LOGGER = logger
        Bukkit.getPluginManager().registerEvents(RegisterListener, this)
    }

    override fun disable() {
    }

    companion object: CompanionBase()
}
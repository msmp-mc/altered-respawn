package world.anhgelus.msmp.alteredrespawn

import org.bukkit.Bukkit
import world.anhgelus.msmp.alteredrespawn.listener.RegisterListener
import world.anhgelus.msmp.msmpcore.PluginBase

class AlteredRespawn: PluginBase() {
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
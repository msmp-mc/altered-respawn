package world.anhgelus.msmp.alteredrespawn.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import world.anhgelus.msmp.alteredrespawn.api.Stack

object RegisterListener: Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDamage(event: EntityDamageEvent){
        Stack.deathEvent(event)
    }

    @EventHandler
    fun forRespawn(event: PlayerInteractAtEntityEvent){
        Stack.respawnEvent(event)
    }

    @EventHandler
    fun avoidPlace(event: BlockPlaceEvent){
        Stack.cancelPlace(event)
    }
}
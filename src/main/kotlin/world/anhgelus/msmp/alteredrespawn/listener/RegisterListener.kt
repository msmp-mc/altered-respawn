package world.anhgelus.msmp.alteredrespawn.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent

object RegisterListener: Listener {
    @EventHandler
    fun onDamage(event: EntityDamageEvent){
        Stack.death(event)
    }
    @EventHandler
    fun forRespawn(event: PlayerInteractAtEntityEvent){
        Stack.respawn(event)
    }
}
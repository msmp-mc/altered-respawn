package world.anhgelus.msmp.alteredrespawn.listener

import org.bukkit.Bukkit
import org.bukkit.entity.Player



var dead: Array<String> = arrayOf()

class StatePlayer {
    fun isDead(player: Player): Boolean {
        return dead.contains(player.displayName)
    }
    fun addDeath(player: Player){
        dead += player.displayName
    }

    fun getDeath(): String{
        return dead.contentToString()
    }
}
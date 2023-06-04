package world.anhgelus.msmp.alteredrespawn

import org.bukkit.GameMode
import org.bukkit.entity.Player
import world.anhgelus.msmp.msmpcore.utils.ChatHelper

class DefaultCondition {
    fun player(player: Player){
        if(player.isOp){
            ChatHelper.sendInfoToPlayer(player, "As administrator you gamemode hasn't been changed")
        } else {
            player.gameMode = GameMode.SURVIVAL
        }
        player.isInvulnerable = false
        player.canPickupItems = true
        player.health = 20.0
        player.foodLevel = 20
    }
}
package world.anhgelus.msmp.alteredrespawn.listener

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import world.anhgelus.msmp.alteredrespawn.DefaultCondition
import world.anhgelus.msmp.msmpcore.player.MPlayerManager
import world.anhgelus.msmp.msmpcore.utils.ChatHelper

class Stack {
    fun death(event: EntityDamageEvent){
        if (event.entityType != EntityType.PLAYER) return

        val player = event.entity as Player

        if (player.health - event.damage > 0) {
            return
        }

        val stack = ItemStack(Material.STRUCTURE_VOID)
        val stackMeta: ItemMeta = stack.itemMeta!!
        stackMeta.setDisplayName("§d" + player.name + "'s STACK")
        stack.setItemMeta(stackMeta)

        val mplayer = MPlayerManager.get(player)
        mplayer.died(event) { mplayer, event ->
            player.sendHurtAnimation(5F)
            event.isCancelled = true
            player.health = 20.0
            player.canPickupItems = false
            var n: Int = 0

            for (i in 0 until player.inventory.size) {
                val items = player.inventory.contents[n]
                Bukkit.getLogger().info(items.toString())
                n++
                if (items != null) {
                    player.world.dropItemNaturally(player.location, items)
                }
            }

            player.world.dropItem(player.location, stack)
            player.inventory.clear()
        }
    }
    fun respawn(event: PlayerInteractAtEntityEvent){
        val player = event.player as Player
        val mplayer = MPlayerManager.get(player)

        if (player.inventory.itemInMainHand.type != Material.STRUCTURE_VOID) {
            return
        }
        Bukkit.getOnlinePlayers().forEach {
            if(!player.inventory.itemInMainHand.itemMeta!!.displayName.contains(it.displayName)){
                return
            }
            if(mplayer.isAlive()){
                ChatHelper.sendInfoToPlayer(player, "This player is not dead!")
                return@forEach
            }
            mplayer.gainANewLife {
                it.player.teleport(event.rightClicked.location)
                DefaultCondition().player(it.player)
            }

        }
    }
}

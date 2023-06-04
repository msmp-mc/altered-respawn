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
import world.anhgelus.msmp.msmpcore.utils.ChatHelper

class pile {
    fun death(event: EntityDamageEvent){
        if (event.entityType != EntityType.PLAYER) return

        val player = event.entity as Player

        if (player.health - event.damage > 0) {
            return
        }

        val item = ItemStack(Material.STRUCTURE_VOID)
        val itemMeta: ItemMeta = item.itemMeta!!

        itemMeta.setDisplayName("§d" + player.name + "'s PILE")
        item.setItemMeta(itemMeta)

        ChatHelper.send(player.name + " is dead!")
        player.sendHurtAnimation(5F)
        event.isCancelled = true
        player.health = 20.0
        player.canPickupItems = false
        var n: Int = 0

        for (i in 0 until player.inventory.size) {
            val item = player.inventory.contents.get(n)
            if (item != null) {
                player.world.dropItemNaturally(player.location, item)
            }
        }

        player.world.dropItem(player.location, item)
        player.inventory.clear()
        StatePlayer().addDeath(player)
    }
    fun respawn(event: PlayerInteractAtEntityEvent){
        val player = event.player as Player

        if (player.inventory.itemInMainHand.type != Material.STRUCTURE_VOID) {
            return
        }
        Bukkit.getOnlinePlayers().forEach {
            if(!player.inventory.itemInMainHand.itemMeta!!.displayName.contains(it.displayName)){
                return
            }
            if(!StatePlayer().isDead(it)){
                ChatHelper.sendInfoToPlayer(player, "This player is not dead!")
            }
            it.teleport(event.rightClicked.location)
            DefaultCondition().player(it)
        }
    }
}

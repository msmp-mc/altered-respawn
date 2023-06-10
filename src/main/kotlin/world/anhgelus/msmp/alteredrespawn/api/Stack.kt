package world.anhgelus.msmp.alteredrespawn.api

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import world.anhgelus.msmp.alteredrespawn.AlteredRespawn
import world.anhgelus.msmp.msmpcore.player.MPlayerManager
import world.anhgelus.msmp.msmpcore.utils.ChatHelper

object Stack {
    /**
     * Handle the death event
     *
     * @param event The event to handle
     */
    fun deathEvent(event: EntityDamageEvent){
        if (event.entityType != EntityType.PLAYER) return

        val player = event.entity as Player

        if (player.health - event.damage > 0) {
            return
        }

        val stack = ItemStack(Material.STRUCTURE_VOID)
        val stackMeta: ItemMeta = stack.itemMeta!!
        stackMeta.setDisplayName("§d" + player.name + "'s STACK")
        stack.itemMeta = stackMeta

        val mplayer = MPlayerManager.get(player)
         mplayer.died(event) { _, _ ->
            player.sendHurtAnimation(5F)
            event.isCancelled = true
            player.health = 20.0
            player.canPickupItems = false
            var n = 0

            for (i in 0 until player.inventory.size) {
                val items = player.inventory.contents[n]
                n++
                if (items != null) {
                    player.world.dropItemNaturally(player.location, items)
                }
            }

            player.world.dropItem(player.location, stack)
            player.inventory.clear()
            player.gameMode = GameMode.SPECTATOR
            player.world.strikeLightningEffect(player.location)
        }
    }

    fun cancelPlace(event: BlockPlaceEvent){
        event.isCancelled = event.block.type == Material.STRUCTURE_VOID
    }

    /**
     * Handle the respawn event
     *
     * @param event The event to handle
     */
    fun respawnEvent(event: PlayerInteractAtEntityEvent){
        val player = event.player
        val name = player.inventory.itemInMainHand.itemMeta!!.displayName

        if (player.inventory.itemInMainHand.type != Material.STRUCTURE_VOID) {
            return
        }

        val respawned = Bukkit.getPlayer(name.split("'")[0].removePrefix("§d"))!!
        val mplayer = MPlayerManager.get(respawned)
        if(mplayer.isAlive()){
            ChatHelper.sendInfoToPlayer(respawned, "This player is not dead!")
            return
        }

        val item = player.inventory.itemInMainHand
        if (item.amount == 1) {
            player.inventory.removeItem(item)
        } else {
            item.amount--
        }

        var timer = 4
        for (i in 0..timer) {
            Bukkit.getScheduler().runTaskLater(AlteredRespawn.INSTANCE, Runnable {
                if(i != 4){
                    respawned.sendTitle(timer.toString(), null, 0, 20, 0)

                } else {
                    mplayer.gainANewLife {
                        it.player.teleport(event.rightClicked.location)
                        respawned.sendTitle("respawned !", null, 0, 20, 0)
                        mplayer.toDefaultCondition()
                        respawned.world.strikeLightningEffect(event.rightClicked.location)
                    }
                }
                timer -= 1
                respawned.playSound(respawned, Sound.BLOCK_NOTE_BLOCK_BELL, 5F, i.toFloat())
            }, (i/0.05-20).toLong())
        }
    }

}


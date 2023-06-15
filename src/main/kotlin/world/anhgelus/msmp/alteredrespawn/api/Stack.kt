package world.anhgelus.msmp.alteredrespawn.api

import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import world.anhgelus.msmp.alteredrespawn.AlteredRespawn
import world.anhgelus.msmp.msmpcore.player.MPlayerManager
import world.anhgelus.msmp.msmpcore.utils.ChatHelper
import kotlin.math.absoluteValue

object Stack {
    private val specateTo = mutableMapOf<Player, Player>()

    /**
     * Handle the death event
     *
     * @param event The event to handle
     */
    fun deathEvent(event: EntityDamageEvent) {
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
            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
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
            spectate(player)
        }
    }

    fun cancelPlace(event: BlockPlaceEvent) {
        event.isCancelled = event.block.type == Material.STRUCTURE_VOID
    }

    /**
     * Handle the respawn event
     *
     * @param event The event to handle
     */
    fun respawnEvent(event: PlayerInteractAtEntityEvent) {
        val player = event.player

        if (event.rightClicked.type != EntityType.ARMOR_STAND) return
        event.isCancelled = true
        if (player.inventory.itemInMainHand.type != Material.STRUCTURE_VOID) return

        val name = player.inventory.itemInMainHand.itemMeta!!.displayName
        val respawned = Bukkit.getPlayer(name.split("'")[0].removePrefix("§d"))!!
        val mplayer = MPlayerManager.get(respawned)

        if (mplayer.isAlive()) {
            ChatHelper.sendInfoToPlayer(respawned, "This player is not dead!")
            return
        }

        val item = player.inventory.itemInMainHand
        if (item.amount == 1) {
            player.inventory.removeItem(item)
        } else {
            item.amount--
        }
        if ((event.rightClicked as ArmorStand).equipment?.helmet?.type != Material.LEATHER_HELMET) return
        val helmet = ((event.rightClicked as ArmorStand).equipment?.helmet?.itemMeta as LeatherArmorMeta)
        event.rightClicked.remove()
        var timer = 4
        for (i in 0..timer) {
            Bukkit.getScheduler().runTaskLater(AlteredRespawn.INSTANCE, Runnable {
                if (i != 4) {
                    respawned.sendTitle(timer.toString(), null, 0, 20, 0)

                } else {
                    mplayer.gainANewLife {
                        it.player.teleport(event.rightClicked.location)
                        respawned.sendTitle("respawned !", null, 0, 20, 0)
                        mplayer.toDefaultCondition()
                        respawned.world.strikeLightningEffect(event.rightClicked.location)
                        respawned.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(80, 5))
                        respawned.addPotionEffect(PotionEffectType.SLOW.createEffect(80, 10))
                        respawned.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(160, 5))
                        respawned.addPotionEffect(PotionEffectType.CONFUSION.createEffect(160, 1))
                        this.setTier(respawned, helmet.color.asARGB())
                        if (!specateTo.containsKey(it.player)) return@gainANewLife
                        specateTo.remove(it.player, specateTo[it.player])

                        //if(!specateTo.containsKey(player)) return@gainANewLife
                        //Bukkit.broadcastMessage("Won a new life")
                    }
                }
                timer -= 1
                respawned.playSound(respawned, Sound.BLOCK_NOTE_BLOCK_BELL, 5F, i.toFloat())
            }, (i / 0.05 - 20).toLong())
        }


    }

    fun setTier(player: Player, helmet: Int) {
        val section = AlteredRespawn.ConfigAPI.getConfig("config").get()
                .getConfigurationSection("tiers")!!
        val modifications: TierModifications
        when (helmet) {
            DyeColor.WHITE.color.asARGB() -> {
                modifications = TierModifications.fromConfig(1, section.getConfigurationSection("1")!!)
            }

            DyeColor.GRAY.color.asARGB() -> {
                modifications = TierModifications.fromConfig(2, section.getConfigurationSection("2")!!)
            }

            DyeColor.GREEN.color.asARGB() -> {
                modifications = TierModifications.fromConfig(3, section.getConfigurationSection("3")!!)
            }

            DyeColor.BLUE.color.asARGB() -> {
                modifications = TierModifications.fromConfig(4, section.getConfigurationSection("4")!!)
            }

            else -> {
                modifications = TierModifications.fromConfig(5, section.getConfigurationSection("5")!!)
            }
        }
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = modifications.maxHealth.toDouble()
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.baseValue = modifications.speed.toDouble()
        if (modifications.resistance >= 0) {
            player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(PotionEffect.INFINITE_DURATION, modifications.resistance))
        }
        if (modifications.strength >= 0) {
            player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(PotionEffect.INFINITE_DURATION, modifications.strength))
        }
    }

    private fun spectate(player: Player){
        player.getNearbyEntities(500.00,500.00,500.00).forEach {
            if (it.type != EntityType.PLAYER) return@forEach
            if((it as Player).gameMode != GameMode.SURVIVAL) return@forEach
            player.teleport(it.location)
            specateTo[player] = it
        }
    }

    fun task(){
        Bukkit.getScheduler().runTaskTimer(AlteredRespawn.INSTANCE, fun(){
            Bukkit.getOnlinePlayers().forEach {
                if(!specateTo.containsKey(it)) return@forEach
                val spectatorLocation = it.location
                val playerLocation = specateTo[it]!!.location
                val x = (spectatorLocation.x - playerLocation.x).absoluteValue
                val y = (spectatorLocation.y - playerLocation.y).absoluteValue
                val z = (spectatorLocation.z - playerLocation.z).absoluteValue
                val area = AlteredRespawn.ConfigAPI.getConfig("config").get().getInt("spectator.area")
                if(!(x > area || y > area || z > area)) return@forEach
                it.teleport(playerLocation)
            }
        }, 0, 10L)
    }
}
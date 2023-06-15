package world.anhgelus.msmp.alteredrespawn.api

import org.bukkit.configuration.ConfigurationSection

/**
 * Tier Modifications representation
 *
 * @param tier Tier of the modification
 * @param maxHealth The maximum health of the player
 * @param speed The speed of the player
 * @param resistance The resistance of the player
 * @param strength The strength of the player
 */
data class TierModifications(
        val tier: Int,
        val maxHealth: Float,
        val speed: Float,
        val resistance: Int,
        val strength: Int,
) {
    /**
     * Save a tier in the config
     *
     * @param section The section where tiers are stored
     */
    fun saveInConfig(section: ConfigurationSection) {
        section.set("$tier.max_health", maxHealth)
        section.set("$tier.speed", speed)
        section.set("$tier.resistance", resistance)
        section.set("$tier.strength", strength)
    }

    companion object {
        /**
         * Get a tier modifications from the config
         *
         * @param tier Tier of the modification
         * @param section Section where the tier is stored
         */
        fun fromConfig(tier: Int, section: ConfigurationSection): TierModifications {
            val maxHealth = section.getDouble("max_health").toFloat()
            val speed = section.getDouble("speed").toFloat()
            val resistance = section.getInt("resistance")
            val strength = section.getInt("strength")
            return TierModifications(tier, maxHealth, speed, resistance, strength)
        }
    }
}
//     max_health:
//    speed:    resistance:

package eu.mrlubau.authmeengine.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin

class EditListener : Listener {

    private val editingPlayers = mutableSetOf<Player>()

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player: Player = event.player
        if (!isEditing(player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player: Player = event.player
        if (!isEditing(player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && event.entity is Player) {
            val attacker: Player = event.damager as Player
            if (!isEditing(attacker)) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player: Player = event.player
        if (!isEditing(player)) {
            event.isCancelled = true
        }
    }


    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        event.foodLevel = 20
    }


    private fun isEditing(player: Player): Boolean {
        return editingPlayers.contains(player)
    }

    fun enableEditing(player: Player) {
        editingPlayers.add(player)
    }

    fun disableEditing(player: Player) {
        editingPlayers.remove(player)
    }

    fun isPlayerEditing(player: Player): Boolean {
        return isEditing(player)
    }
}

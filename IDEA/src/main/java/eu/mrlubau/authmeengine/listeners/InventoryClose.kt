package eu.mrlubau.authmeengine.listeners

import eu.mrlubau.authmeengine.Authme_engine
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class InventoryClose : Listener {

    @EventHandler
    fun inventoryCloseEvent(event: InventoryCloseEvent) {
        val playerUUID = event.player.uniqueId

        if (Authme_engine.guiMap.containsKey(playerUUID)) {
            Authme_engine.guiMap.remove(playerUUID)
        }
    }

    @EventHandler
    fun inventoryClickEvent(event: InventoryClickEvent) {
        if (event.whoClicked is Player) {
            val player = event.whoClicked as Player
            val playerUUID = player.uniqueId

            if (Authme_engine.guiMap.containsKey(playerUUID)) {
                if (event.isShiftClick) {
                    event.isCancelled = true
                    player.updateInventory()
                }
            }
        }
    }
}
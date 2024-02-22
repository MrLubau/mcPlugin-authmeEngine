package eu.mrlubau.authmeengine.listeners

import eu.mrlubau.authmeengine.Authme_engine
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement


class InventoryClick(private val plugin: JavaPlugin) : Listener {

    init {
        plugin.saveDefaultConfig()
    }

    @EventHandler
    fun inventoryClickEvent(event: InventoryClickEvent) {
        val config: FileConfiguration = plugin.config
        val player = event.whoClicked
        val playerUUID = event.whoClicked.uniqueId
        val prefix = config.getString("prefix", "Error")
        val pcolor = config.getString("pcolor", "Error")
        val scolor = config.getString("scolor", "Error")
        val tcolor = config.getString("tcolor", "Error")
        val playerName = player.name
        val rules = "1"
        val server = "lobby"

        val dbchost = config.getString("database.host", "Error")
        val dbcusername = config.getString("database.username", "Error")
        val dbcdatabase = config.getString("database.database", "Error")
        val dbcpassword = config.getString("database.password", "Error")

        val dburl = "jdbc:mysql://$dbchost/$dbcdatabase"
        val dbuser = "$dbcusername"
        val dbpassword = "$dbcpassword"

        if(event.inventory == Authme_engine.guiMap[playerUUID]) {
            if(event.currentItem == null) return

            val clickedSlot = event.slot

            when(clickedSlot) {
                24 -> {
                    player.sendMessage("$prefix Pro dokončení registrace je potřeba potvrdit pravidla která naleznete zde:")
                    player.sendMessage("$scolor https://hype-play.cz/pravidla")
                    player.sendMessage("$scolor    ")
                    player.sendMessage("$pcolor Pro otevření menu napište /autorizace")
                    event.inventory.close()
                }
                20 -> {
                    event.inventory.close()
                    val connection: Connection = DriverManager.getConnection(dburl, dbuser, dbpassword)
                    val sql = "UPDATE authme SET rules = $rules WHERE username = '$playerName'"
                    val statement: Statement = connection.createStatement()
                    statement.executeUpdate(sql)

                    val insertDataQuery = "INSERT INTO request (username, server) VALUES ('$playerName', '$server')"
                    val insertDataStatement: Statement = connection.createStatement()
                    insertDataStatement.execute(insertDataQuery)
                    connection.close()
                    player.sendMessage("$prefix Pravidla byla potvrzena!")
                }
                else -> {}
            }
            event.isCancelled = true
        }
    }
}
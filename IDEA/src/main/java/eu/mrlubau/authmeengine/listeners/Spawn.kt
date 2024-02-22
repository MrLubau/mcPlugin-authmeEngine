package eu.mrlubau.authmeengine.listeners

import org.bukkit.Server
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.json.simple.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class Spawn(private val plugin: JavaPlugin) : Listener {

    init {
        plugin.saveDefaultConfig()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage = null
        event.setQuitMessage(null);
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.setJoinMessage(null);
        event.joinMessage(null);
        val config: FileConfiguration = plugin.config
        val locplayer = event.player
        val prefix = config.getString("prefix", "Error")
        val pcolor = config.getString("pcolor", "Error")
        val scolor = config.getString("scolor", "Error")
        val tcolor = config.getString("tcolor", "Error")
        val servername = config.getString("server", "Error")
        val joinspawn = config.getString("joinspawn", "false")
        val joinmessage = config.getString("joinmessage", "true")
        val invisible = config.getString("invisible", "false")
        val joinedPlayer: Player = event.player
        val playerName = event.player.name
        val player = event.player

        val dbchost = config.getString("database.host", "Error")
        val dbcusername = config.getString("database.username", "Error")
        val dbcdatabase = config.getString("database.database", "Error")
        val dbcpassword = config.getString("database.password", "Error")

        val dburl = "jdbc:mysql://$dbchost/$dbcdatabase"
        val dbuser = "$dbcusername"
        val dbpassword = "$dbcpassword"
        val connection: Connection = DriverManager.getConnection(dburl, dbuser, dbpassword)
        val query = "SELECT * FROM authme WHERE username = ?"





        if (invisible == "true") {
            for (player in joinedPlayer.server.onlinePlayers) {
                if (player != joinedPlayer) {
                    player.hidePlayer(plugin, joinedPlayer)
                }
            }

            for (otherPlayer in joinedPlayer.server.onlinePlayers) {
                if (otherPlayer != joinedPlayer) {
                    joinedPlayer.hidePlayer(plugin, otherPlayer)
                }
            }
        }

        if (joinmessage == "true") {
            locplayer.sendMessage("$tcolor ===========================================")
            locplayer.sendMessage("$pcolor ")
            locplayer.sendMessage("$pcolor Vítej na serveru $scolor$servername")
            locplayer.sendMessage("$pcolor ")
            locplayer.sendMessage("$tcolor ===========================================")
        }


        try {
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, playerName)

            val resultSet = preparedStatement.executeQuery()

            if (!resultSet.next()) {
                locplayer.sendMessage("$prefix Použij $tcolor>> $scolor/register <heslo> <znovu heslo>")
            } else {
                locplayer.sendMessage("$prefix Použij $tcolor>> $scolor/login <heslo>")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }


        if (joinspawn == "true") {
            val x = plugin.config.getString("spawn.x")?.toDouble() ?: 0.0
            val y = plugin.config.getString("spawn.y")?.toDouble() ?: 0.0
            val z = plugin.config.getString("spawn.z")?.toDouble() ?: 0.0
            val worldName = plugin.config.getString("spawn.world") ?: "world"
            val pitch = plugin.config.getString("spawn.pitch")?.toFloat() ?: 0.0f
            val yaw = plugin.config.getString("spawn.yaw")?.toFloat() ?: 0.0f

            val world = plugin.server.getWorld(worldName)
            if (world != null) {
                val location = locplayer.location
                location.x = x
                location.y = y
                location.z = z
                location.world = world
                location.pitch = pitch
                location.yaw = yaw

                locplayer.teleport(location)
            } else {
                locplayer.sendMessage("$prefix Svět $scolor$worldName$pcolor nebyl nalezen")
            }
            } else if (joinspawn == "false") {

            } else {
            locplayer.sendMessage("$prefix Neplatná hodnota $scolor$joinspawn$pcolor u joinspawn v config.yml")
            }
    }
}
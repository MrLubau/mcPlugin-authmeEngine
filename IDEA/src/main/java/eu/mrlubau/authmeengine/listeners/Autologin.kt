package eu.mrlubau.authmeengine.listeners

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class Autologin(private val plugin: JavaPlugin) : Listener {
    init {
        plugin.saveDefaultConfig()
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
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

        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, playerName)

        val resultSet = preparedStatement.executeQuery()

        if (!resultSet.next()) { return }
        val origoValue = resultSet.getInt("origo")

        if (origoValue == 0) { return }

        val playerip = locplayer.address.address.hostAddress
        val playeripedit = "/$playerip"
        val databaseIpAddress = resultSet.getString("ipadress")

        if (databaseIpAddress != playeripedit) { locplayer.kickPlayer("Autorizace Autologinu selhala"); return }

        val server = "lobby"

        locplayer.sendMessage("$prefix Automaticky přihlášen!")
        val insertDataQuery = "INSERT INTO request (username, server) VALUES ('$playerName', '$server')"
        val insertDataStatement: Statement = connection.createStatement()
        insertDataStatement.execute(insertDataQuery)



    }

}
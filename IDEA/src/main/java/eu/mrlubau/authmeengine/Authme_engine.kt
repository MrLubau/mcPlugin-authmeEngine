package eu.mrlubau.authmeengine

import eu.mrlubau.authmeengine.commands.*
import eu.mrlubau.authmeengine.listeners.*
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*

class Authme_engine : JavaPlugin() {

    companion object {

        val guiMap: MutableMap<UUID, Inventory> = mutableMapOf()
    }

    private lateinit var editListener: EditListener


    override fun onEnable() {
        saveDefaultConfig()

        editListener = EditListener()

        val config: FileConfiguration = config
        val pluginText = config.getString("plugin", "Error")

        logger.info("===================================")
        logger.info("        $pluginText plugin       ")
        logger.info("                                   ")
        logger.info("Loading.")
        logger.info("Loading..")
        logger.info("Loading...")
        logger.info("Loaded!")
        logger.info("===================================")

        registerCommands()
        registerListeners()

        val dbchost = config.getString("database.host", "Error")
        val dbcusername = config.getString("database.username", "Error")
        val dbcdatabase = config.getString("database.database", "Error")
        val dbcpassword = config.getString("database.password", "Error")

        val dburl = "jdbc:mysql://$dbchost/$dbcdatabase"
        val dbuser = "$dbcusername"
        val dbpassword = "$dbcpassword"

        val connection: Connection = DriverManager.getConnection(dburl, dbuser, dbpassword)

        val createAuthme = "CREATE TABLE IF NOT EXISTS authme (id int(11) NOT NULL AUTO_INCREMENT, username varchar(255) NOT NULL, password varchar(255) NOT NULL, email varchar(256) NULL DEFAULT NULL, rules varchar(16) NOT NULL, ipadress varchar(255) NOT NULL, nick varchar(256) NULL DEFAULT NULL, ban varchar(16) NOT NULL, rank varchar(16) NOT NULL, tokens varchar(256) NOT NULL, origo varchar(16) NOT NULL, origouuid varchar(256) NULL DEFAULT NULL, PRIMARY KEY (id))"
        val createAuthmeStatement: Statement = connection.createStatement()
        createAuthmeStatement.execute(createAuthme)

        val createRequest = "CREATE TABLE IF NOT EXISTS request (id int(11) NOT NULL AUTO_INCREMENT, username varchar(255) NOT NULL, server varchar(255) NOT NULL, minigame varchar(255) NULL DEFAULT NULL, PRIMARY KEY (id))"
        val createRequestStatement: Statement = connection.createStatement()
        createRequestStatement.execute(createRequest)
    }

    private fun registerListeners() {
        server.pluginManager.registerEvents(Spawn(this), this)
        server.pluginManager.registerEvents(Autologin(this), this)
        server.pluginManager.registerEvents(editListener, this)
        Bukkit.getServer().pluginManager.registerEvents(InventoryClick(this), this)
        Bukkit.getServer().pluginManager.registerEvents(InventoryClose(), this)

        logger.info("Registered Listeners")
    }

    private fun registerCommands() {
        getCommand("setspawn")?.setExecutor(setspawn(this))
        getCommand("engine")?.setExecutor(engine(this))
        getCommand("engine-reload")?.setExecutor(reload(this))
        getCommand("register")?.setExecutor(register(this))
        getCommand("autorizace")?.setExecutor(autorizace(this))
        getCommand("edit")?.setExecutor(edit(editListener, this))
        getCommand("login")?.setExecutor(login(this))

        logger.info("Registered Commands")
    }

    override fun onDisable() {
        val config: FileConfiguration = config
        val pluginText = config.getString("plugin", "Error")

        logger.info("===================================")
        logger.info("        $pluginText plugin       ")
        logger.info("                                   ")
        logger.info("Unloaded!")
        logger.info("===================================")
    }
}

package eu.mrlubau.authmeengine.commands

import eu.mrlubau.authmeengine.Authme_engine
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.mindrot.jbcrypt.BCrypt
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class login(private val plugin: JavaPlugin) : CommandExecutor {

    init {
        plugin.saveDefaultConfig()
    }

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        val config: FileConfiguration = plugin.config
        val prefix = config.getString("prefix", "Error")
        val pcolor = config.getString("pcolor", "Error")
        val scolor = config.getString("scolor", "Error")
        val tcolor = config.getString("tcolor", "Error")
        val playerName = sender.name
        val server = "lobby"

        val dbchost = config.getString("database.host", "Error")
        val dbcusername = config.getString("database.username", "Error")
        val dbcdatabase = config.getString("database.database", "Error")
        val dbcpassword = config.getString("database.password", "Error")

        val dburl = "jdbc:mysql://$dbchost/$dbcdatabase"
        val dbuser = "$dbcusername"
        val dbpassword = "$dbcpassword"

        if (sender !is Player) {
            sender.sendMessage("$prefix Tento příkaz může použít pouze hráč")
            return true
        }


        val connection: Connection = DriverManager.getConnection(dburl, dbuser, dbpassword)
        val query = "SELECT * FROM authme WHERE username = ?"

        try {
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, playerName)
            val resultSet = preparedStatement.executeQuery()

            if (!resultSet.next()) {
                sender.sendMessage("$prefix Ještě nejsi zaregistrován!")
                return false
            } else {

                if (args.size < 1) {
                    sender.sendMessage("$prefix Použij $tcolor>> $scolor/login <heslo>")
                    return true
                }

                val password = args[0]
                val rulesValue = resultSet.getInt("rules")

                val hashedPasswordFromDatabase = resultSet.getString("password")

                if (!BCrypt.checkpw(password, hashedPasswordFromDatabase)) {
                    sender.sendMessage("$prefix Špatné heslo")
                    return false
                }


                if (rulesValue == 0) {

                    val inventory = Bukkit.createInventory(sender, 54, Component.text("七七七七七七七七ㇺ").color(TextColor.color(255, 255, 255)))

                    val confirm = ItemStack(Material.DIAMOND)
                    val confirmMeta = confirm.itemMeta
                    confirmMeta.displayName(Component.text("$scolor Potvrdit"))
                    confirmMeta.setCustomModelData(1)
                    val confirmlore = mutableListOf<Component>()
                    confirmlore.add(Component.text("$tcolor ============================="))
                    confirmlore.add(Component.text("$pcolor Tímto tlačítkem potvrzujete že jste"))
                    confirmlore.add(Component.text("$pcolor si přečetli pravidla a souhlasíte s nimi!"))
                    confirmlore.add(Component.text("$tcolor ============================="))
                    confirmMeta.lore(confirmlore)
                    confirm.itemMeta = confirmMeta


                    val help = ItemStack(Material.DIAMOND)
                    val helpMeta = help.itemMeta
                    helpMeta.displayName(Component.text("$scolor Více informací"))
                    helpMeta.setCustomModelData(2)
                    val helplore = mutableListOf<Component>()
                    helplore.add(Component.text("$tcolor ============================="))
                    helplore.add(Component.text("$pcolor Po registraci je potřeba potvrdit"))
                    helplore.add(Component.text("$pcolor pravidla!"))
                    helplore.add(Component.text("$pcolor "))
                    helplore.add(Component.text("$pcolor Pravidla naleznete zde:"))
                    helplore.add(Component.text("$scolor https://hype-play.cz/pravidla"))
                    helplore.add(Component.text("$tcolor ============================="))
                    helpMeta.lore(helplore)
                    help.itemMeta = helpMeta

                    inventory.setItem(20, confirm)
                    inventory.setItem(24, help)

                    Authme_engine.guiMap[sender.uniqueId] = inventory

                    sender.openInventory(inventory)
                    return false
                }

                val ipValue = resultSet.getString("ipadress")
                val ipAddress = sender.address.address.toString()

                if (ipValue != ipAddress) {
                    val updateSql = "UPDATE authme SET ipadress = ? WHERE username = ?"

                    try {
                        connection.use { conn ->
                            conn.prepareStatement(updateSql).use { preparedStatement ->
                                preparedStatement.setString(1, ipAddress)
                                preparedStatement.setString(2, playerName)
                                preparedStatement.executeUpdate()
                            }
                        }
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    }
                }

                sender.sendMessage("$prefix Přihlášen!")
                val insertDataQuery = "INSERT INTO request (username, server) VALUES ('$playerName', '$server')"
                val insertDataStatement: Statement = connection.createStatement()
                insertDataStatement.execute(insertDataQuery)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }


        return true
    }
}
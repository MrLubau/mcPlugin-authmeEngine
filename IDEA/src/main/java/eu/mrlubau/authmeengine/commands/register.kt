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

class register(private val plugin: JavaPlugin) : CommandExecutor {

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

        val dbchost = config.getString("database.host", "Error")
        val dbcusername = config.getString("database.username", "Error")
        val dbcdatabase = config.getString("database.database", "Error")
        val dbcpassword = config.getString("database.password", "Error")

        val dburl = "jdbc:mysql://$dbchost/$dbcdatabase"
        val dbuser = "$dbcusername"
        val dbpassword = "$dbcpassword"

        val connection: Connection = DriverManager.getConnection(dburl, dbuser, dbpassword)
        val query = "SELECT * FROM authme WHERE username = ?"

        try {
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, playerName)

            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                sender.sendMessage("$prefix Již jsi zaregistrován!")
                return false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }




        if (sender !is Player) {
            sender.sendMessage("$prefix Tento příkaz může použít pouze hráč")
            return true
        }

        if (args.size < 2) {
            sender.sendMessage("$prefix Použij $tcolor>> $scolor/register <heslo> <znovu heslo>")
            return true
        }

        val password = args[0]
        val confirmPassword = args[1]

        if (password != confirmPassword) {
            sender.sendMessage("$prefix Hesla se neshodují")
            return true
        }



        val salt = BCrypt.gensalt(10)
        val hashedPassword = BCrypt.hashpw(password, salt)

        val ipAddress = sender.address.address.toString()

        val insertDataQuery = "INSERT INTO authme (username, password, ipadress, rules, rank, ban, tokens, origo) VALUES ('$playerName', '$hashedPassword', '$ipAddress', 0, 0, 0, 0, 0)"
        val insertDataStatement: Statement = connection.createStatement()
        insertDataStatement.execute(insertDataQuery)
        connection.close()
        sender.sendMessage("$prefix Účet vytvořen, je potřeba potvrdit pravidla")
        sender.sendMessage("$scolor https://hype-play.cz/pravidla")



        val inventory = Bukkit.createInventory(sender, 54, Component.text("七七七七七七七七ㇺ").color(TextColor.color(255, 255, 255)))




        val confirm = ItemStack(Material.DIAMOND)
        val confirmMeta = confirm.itemMeta
        confirmMeta.displayName(Component.text("§2Potvrdit"))
        confirmMeta.setCustomModelData(1)
        val confirmlore = mutableListOf<Component>()
        confirmlore.add(Component.text("§8 ============================="))
        confirmlore.add(Component.text("§7 Tímto tlačítkem potvrzujete že jste"))
        confirmlore.add(Component.text("§7 si přečetli pravidla a souhlasíte s nimi!"))
        confirmlore.add(Component.text("§8 ============================="))
        confirmMeta.lore(confirmlore)
        confirm.itemMeta = confirmMeta


        val help = ItemStack(Material.DIAMOND)
        val helpMeta = help.itemMeta
        helpMeta.displayName(Component.text("§2Více informací"))
        helpMeta.setCustomModelData(2)
        val helplore = mutableListOf<Component>()
        helplore.add(Component.text("§8 ============================="))
        helplore.add(Component.text("§7 Po registraci je potřeba potvrdit"))
        helplore.add(Component.text("§7 pravidla!"))
        helplore.add(Component.text("§7 "))
        helplore.add(Component.text("§7 Pravidla naleznete zde:"))
        helplore.add(Component.text("§2 https://hype-play.cz/pravidla"))
        helplore.add(Component.text("§8 ============================="))
        helpMeta.lore(helplore)
        help.itemMeta = helpMeta

        inventory.setItem(20, confirm)
        inventory.setItem(24, help)

        Authme_engine.guiMap[sender.uniqueId] = inventory

        sender.openInventory(inventory)



        return true
    }
}
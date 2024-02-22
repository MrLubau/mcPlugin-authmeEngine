package eu.mrlubau.authmeengine.commands

import eu.mrlubau.authmeengine.listeners.EditListener
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class edit(private val editListener: EditListener, private val plugin: JavaPlugin) : CommandExecutor {

    init {
        plugin.saveDefaultConfig()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val config: FileConfiguration = plugin.config
        val prefix = config.getString("prefix", "Error")
        val pcolor = config.getString("pcolor", "Error")
        val scolor = config.getString("scolor", "Error")
        val tcolor = config.getString("tcolor", "Error")
        val error = config.getString("error", "Error")

        if(!sender.isOp) {sender.sendMessage("$prefix $error");return false;}

        if (sender is Player) {
            if (editListener.isPlayerEditing(sender)) {
                editListener.disableEditing(sender)
                sender.sendMessage("$prefix Edit mode vypnut")
                sender.gameMode = GameMode.SURVIVAL
            } else {
                editListener.enableEditing(sender)
                sender.sendMessage("$prefix Edit mode zapnut")
                sender.gameMode = GameMode.CREATIVE
            }
        } else {
            sender.sendMessage("$prefix Tento příkaz může používat pouze hráč")
        }

        return true
    }
}
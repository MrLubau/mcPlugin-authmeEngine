package eu.mrlubau.authmeengine.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class reload(private val plugin: JavaPlugin) : CommandExecutor {

    init {
        plugin.saveDefaultConfig()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val config: FileConfiguration = plugin.config
        val prefix = config.getString("prefix", "Error")
        val error = config.getString("error", "Error")

        if(!sender.hasPermission("engine.administrator")) {sender.sendMessage("$prefix $error");return false;}

        plugin.reloadConfig()
        sender.sendMessage("$prefix Plugin se úspěšně reloadoval")

        return true
    }
}
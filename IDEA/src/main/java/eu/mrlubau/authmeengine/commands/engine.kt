package eu.mrlubau.authmeengine.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class engine(private val plugin: JavaPlugin) : CommandExecutor {

    init {
        plugin.saveDefaultConfig()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val config: FileConfiguration = plugin.config
        val engineText = config.getString("engine", "Error")
        val licenseText = config.getString("license", "Error")
        val prefix = config.getString("prefix", "Error")
        val error = config.getString("prefix", "Error")

        sender.sendMessage(Component.text("====================================").color(TextColor.color(64, 64, 64)))
        sender.sendMessage(Component.text("Server is working on:").color(TextColor.color(0, 128, 255)))
        sender.sendMessage(Component.text("     ").color(TextColor.color(0, 128, 255)))
        sender.sendMessage(Component.text("$engineText by lubau").color(TextColor.color(160, 160, 160)))
        sender.sendMessage(Component.text("https://mrlubau.eu/license/$licenseText").color(TextColor.color(0, 128, 255)))
        sender.sendMessage(Component.text("====================================").color(TextColor.color(64, 64, 64)))

        return true
    }
}
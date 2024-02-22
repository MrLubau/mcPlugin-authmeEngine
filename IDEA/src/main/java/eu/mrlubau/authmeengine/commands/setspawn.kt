package eu.mrlubau.authmeengine.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


class setspawn(private val plugin: JavaPlugin) : CommandExecutor {

    init {
        plugin.saveDefaultConfig()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val config: FileConfiguration = plugin.config
        val prefix = config.getString("prefix", "Error")
        val error = config.getString("error", "Error")

        if(!sender.isOp) {sender.sendMessage("$prefix $error");return false;}
        if (sender !is Player) {sender.sendMessage("$prefix Tento příkaz může používat pouze hráč");return false;}

        val player = sender
        plugin.config.set("spawn.world", player.world.name)
        plugin.config.set("spawn.x", player.location.x)
        plugin.config.set("spawn.y", player.location.y)
        plugin.config.set("spawn.z", player.location.z)
        plugin.config.set("spawn.yaw", player.location.yaw)
        plugin.config.set("spawn.pitch", player.location.pitch)
        plugin.saveConfig()

        player.sendMessage("$prefix Spawn byl nastaven na tvoji aktuální pozici")

        return false
    }
}
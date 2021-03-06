package org.oreoprojekt.cube.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.util.cubeUtil;

public class cubeSpawnCommand implements CommandExecutor {
    private CUBE plugin;
    private cubeUtil cubeUtil;

    public cubeSpawnCommand (CUBE plugin) {
        this.plugin = plugin;
        this.cubeUtil = new cubeUtil(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("No console");
            return false;
        }
        Player player = (Player) sender;
        player.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("message.spawn"));
        Location spawn = new Location(player.getWorld(),cubeUtil.halfRoomSize ,3.5 ,cubeUtil.halfRoomSize);
        cubeUtil.clearEffect(player);
        player.teleport(spawn);
        return false;
    }
}

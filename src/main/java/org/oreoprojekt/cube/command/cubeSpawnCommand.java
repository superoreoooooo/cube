package org.oreoprojekt.cube.command;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class cubeSpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("No console");
            return false;
        }
        Player player = (Player) sender;
        player.sendMessage("스폰으로 이동합니다..");
        Location spawn = new Location(player.getWorld(),11.5 ,7 ,11.5 );
        player.teleport(spawn);
        return false;
    }
}

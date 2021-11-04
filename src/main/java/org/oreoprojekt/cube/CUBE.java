package org.oreoprojekt.cube;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.oreoprojekt.cube.command.cubeItemCommand;
import org.oreoprojekt.cube.command.cubeSpawnCommand;
import org.oreoprojekt.cube.listener.cubeListener;
import org.oreoprojekt.cube.manager.cubeYmlManager;
import org.oreoprojekt.cube.util.cubeUtil;

public final class CUBE extends JavaPlugin {

    public cubeYmlManager ymlManager;
    private cubeUtil cubeUtil;

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "CUBE ON");
        getServer().getPluginManager().registerEvents(new cubeListener(this), this);
        getCommand("cube").setExecutor(new cubeItemCommand(this));
        getCommand("spawn").setExecutor(new cubeSpawnCommand());
        this.ymlManager = new cubeYmlManager(this);
        this.cubeUtil = new cubeUtil(this);

        cubeUtil.checkMain();

        cubeUtil.startTimer();
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "CUBE OFF");
        cubeUtil.stopTimer();
    }
}

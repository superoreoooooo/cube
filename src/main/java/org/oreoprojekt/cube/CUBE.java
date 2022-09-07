package org.oreoprojekt.cube;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.oreoprojekt.cube.command.cubeMainCommand;
import org.oreoprojekt.cube.command.cubeSpawnCommand;
import org.oreoprojekt.cube.command.reloadCommand;
import org.oreoprojekt.cube.listener.cubeListener;
import org.oreoprojekt.cube.manager.yml.cubeYmlManager;
import org.oreoprojekt.cube.manager.yml.pDataYmlManager;
import org.oreoprojekt.cube.util.utils.scoreboard;
import org.oreoprojekt.cube.util.cubeUtil;
import org.oreoprojekt.cube.util.utils.checker;

public final class CUBE extends JavaPlugin {
    public cubeYmlManager ymlManager;
    public pDataYmlManager pDataYmlManager;
    private cubeUtil cubeUtil;
    private scoreboard scoreBoard;
    private checker checker;

    @Override
    public void onEnable() {
        this.scoreBoard = new scoreboard(this);
        this.saveDefaultConfig();

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "CUBE ON");
        getServer().getPluginManager().registerEvents(new cubeListener(), this);

        getCommand("cube").setExecutor(new cubeMainCommand(this));
        getCommand("spawn").setExecutor(new cubeSpawnCommand(this));
        getCommand("rl").setExecutor(new reloadCommand());

        this.ymlManager = new cubeYmlManager(this);
        this.pDataYmlManager = new pDataYmlManager(this);
        this.cubeUtil = new cubeUtil(this);
        this.checker = new checker(this);

        cubeUtil.checkMain();
        cubeUtil.startTimer();

        checker.checkerTimer();

        runBoard();

        Bukkit.broadcastMessage(ChatColor.GREEN + "cube system on");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "CUBE OFF");
        this.saveConfig();
        cubeUtil.stopTimer();
    }

    public void runBoard() {
        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                scoreBoard.createBoard(player);
                scoreBoard.timer(player, true);
            }
        }
    }
}

package org.oreoprojekt.cube;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.oreoprojekt.cube.command.cubeMainCommand;
import org.oreoprojekt.cube.command.cubeSpawnCommand;
import org.oreoprojekt.cube.listener.cubeListener;
import org.oreoprojekt.cube.manager.cubeYmlManager;
import org.oreoprojekt.cube.manager.pDataYmlManager;
import org.oreoprojekt.cube.util.cubeScoreBoard;
import org.oreoprojekt.cube.util.cubeUtil;
import org.oreoprojekt.cube.util.utils.cubeUtil_Checker;

public final class CUBE extends JavaPlugin {

    // 2022 01 25 봉인해제

    public cubeYmlManager ymlManager;
    public pDataYmlManager pDataYmlManager;
    private cubeUtil cubeUtil;
    private cubeScoreBoard scoreBoard;
    private cubeUtil_Checker Util_Checker;

    @Override
    public void onEnable() {
        this.scoreBoard = new cubeScoreBoard(this);
        this.saveDefaultConfig();

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "CUBE ON");
        getServer().getPluginManager().registerEvents(new cubeListener(this), this);

        getCommand("cube").setExecutor(new cubeMainCommand(this));
        getCommand("spawn").setExecutor(new cubeSpawnCommand(this));

        this.ymlManager = new cubeYmlManager(this);
        this.pDataYmlManager = new pDataYmlManager(this);
        this.cubeUtil = new cubeUtil(this);
        this.Util_Checker = new cubeUtil_Checker(this);

        cubeUtil.checkMain();

        Util_Checker.checkerTimer();

        //cubeUtil.startTimer();

        runBoard();
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

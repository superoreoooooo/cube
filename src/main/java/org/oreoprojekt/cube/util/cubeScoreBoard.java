package org.oreoprojekt.cube.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.oreoprojekt.cube.CUBE;

public class cubeScoreBoard {
    private CUBE plugin;
    private cubeUtil cubeUtil;

    public cubeScoreBoard(CUBE plugin) {
        this.plugin = plugin;
        this.cubeUtil = new cubeUtil(plugin);
    }

    public void createBoard(Player player) {
        ScoreboardManager scoreboardManager;
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = scoreboard.registerNewObjective("Playerboard", "dummy", "【Project.kr】");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public void scoreBoardList(Player player) {
        createBoard(player);
        Score scoreName = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮이름 : " + player.getName().toString());
        scoreName.setScore(11);
        Score scoreLocation = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮현재 위치 : R" + cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)));
        scoreLocation.setScore(10);
        Score scoreEffect = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮효과 : " + getEffect(player));
        scoreEffect.setScore(9);
        Score scorePass = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮패스 : " + plugin.pDataYmlManager.getConfig().getInt(player.getName() + ".pass"));
        scorePass.setScore(8);
    }

    public String getEffect(Player player) {
        int effectNumber = plugin.ymlManager.getConfig().getInt("room." + cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)) + ".effect");
        switch (effectNumber + 1) {
            case 1:
                return "중력";
            case 2:
                return "무중력";
            case 3:
                return "연막";
            case 4:
                return "어지러움";
            case 5:
                return "배고픔";
            case 6:
                return "피곤";
            case 7:
                return "불운";
            case 8:
                return "행운";
            case 9:
                return "일반";
            default:
                return "미정";
        }
    }

    public int task;

    public void timer(Player player, Boolean on) {
        if (on) {
            task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    scoreBoardList(player);
                }
            }, 0, 10); //1초마다 갱신 (20 * 초) 20틱이 1초
        }
        else {
            Bukkit.getScheduler().cancelTask(task);
        }
    }
}

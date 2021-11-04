package org.oreoprojekt.cube.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
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
        Score scoreLocation = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮현재 위치 : " + cubeUtil.getCubeNumber(cubeUtil.getCubePos(player)));
        scoreLocation.setScore(10);
        Score scoreEffect = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮효과 : " + getEffect(player));
        scoreEffect.setScore(9);
    }

    public String getEffect(Player player) {
        int effectNumber = plugin.ymlManager.getConfig().getInt("room." + cubeUtil.getCubeNumber(cubeUtil.getCubePos(player)) + ".effect");
        switch (effectNumber) {
            case 1:
                return "중력";
            case 2:
                return "무중력";
            case 3:
                return "독가스";
            case 4:
                return "연막";
            case 5:
                return "고통";
            case 6:
                return "어지러움";
            case 7:
                return "배고픔";
            case 8:
                return "피곤";
            case 9:
                return "불운";
            case 10:
                return "행운";
        }
        return "없음";
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

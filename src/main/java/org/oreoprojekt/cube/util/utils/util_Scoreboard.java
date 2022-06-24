package org.oreoprojekt.cube.util.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.enums.Effect;
import org.oreoprojekt.cube.util.cubeUtil;

public class util_Scoreboard {
    private CUBE plugin;
    private org.oreoprojekt.cube.util.cubeUtil cubeUtil;

    public util_Scoreboard(CUBE plugin) {
        this.plugin = plugin;
        this.cubeUtil = new cubeUtil(plugin);
    }

    public void createBoard(Player player) {
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
        Score scoreType = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮타입 : " + getType(player));
        scoreType.setScore(7);
    }

    public String getEffect(Player player) {
        int effectNumber = plugin.ymlManager.getConfig().getInt("room." + cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)) + ".effect");
        switch (effectNumber + 1) {
            case 1:
                return Effect.GRAVITY.getEfName();
            case 2:
                return Effect.WEIGHTLESS.getEfName();
            case 3:
                return Effect.SMOKE.getEfName();
            case 4:
                return Effect.DIZZY.getEfName();
            case 5:
                return Effect.HUNGRY.getEfName();
            case 6:
                return Effect.TIRED.getEfName();
            case 7:
                return Effect.MISFORTUNE.getEfName();
            case 8:
                return Effect.FORTUNE.getEfName();
            case 9:
                return Effect.NORMAL.getEfName();
            default:
                return Effect.ERROR.getEfName();
        }
    }

    public String getType(Player player) {
        int typeNum = plugin.ymlManager.getConfig().getInt("room." + cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)) + ".type");
        switch (typeNum) {
            case 0:
                return "일반";
            case 1:
                return "보스";
            default:
                return "error";
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

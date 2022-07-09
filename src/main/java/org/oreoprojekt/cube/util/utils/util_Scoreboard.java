package org.oreoprojekt.cube.util.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.enums.Effect;
import org.oreoprojekt.cube.enums.CubeType;
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
        Score scoreName = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮이름 : " + player.getName());
        scoreName.setScore(11);
        Score scoreLocation;
        scoreLocation = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮현재 위치 : L" + cubeUtil.getLevel(player) + "C" + cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)));
        scoreLocation.setScore(10);
        /*
        if (cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)) >= 0 && cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)) <= 8) {
            scoreLocation = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮현재 위치 : 스폰");
            scoreLocation.setScore(10);
        }
        else {
            scoreLocation = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮현재 위치 : R" + cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)));
            scoreLocation.setScore(10);
        } */
        Score scoreEffect = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮효과 : " + getEffect(player));
        scoreEffect.setScore(9);
        Score scoreType = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮타입 : " + getType(player));
        scoreType.setScore(8);
        Score scorePass = player.getScoreboard().getObjective("Playerboard").getScore(ChatColor.WHITE + "⎮패스 : " + plugin.pDataYmlManager.getConfig().getInt(player.getName() + ".pass"));
        scorePass.setScore(7);
    }

    public String getEffect(Player player) {
        int effectNum = plugin.ymlManager.getConfig().getInt("cube." + cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)) + ".effect");
        String effectName = Effect.ERROR.getEfName();
        for (Effect name : Effect.values()) {
            if (name.getEfNum() == effectNum) {
                return name.getEfName();
            }
        }
        return Effect.ERROR.getEfName();
    }

    public String getType(Player player) {
        int typeNum = plugin.ymlManager.getConfig().getInt("cube." + cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)) + ".type");
        String typeName = CubeType.ERROR.getName();
        for (CubeType cubeType : CubeType.values()) {
            if (cubeType.getNum() == typeNum) {
                typeName = cubeType.getName();
            }
        }
        for (CubeType type : CubeType.values()) {
            if (type.getName().equals(typeName)) {
                return type.getName();
            }
        }
        return CubeType.ERROR.getName();
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

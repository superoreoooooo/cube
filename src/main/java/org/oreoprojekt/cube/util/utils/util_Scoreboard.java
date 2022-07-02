package org.oreoprojekt.cube.util.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.enums.Effect;
import org.oreoprojekt.cube.enums.RoomType;
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
        int effectNum = plugin.ymlManager.getConfig().getInt("room." + cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)) + ".effect");
        String effectName = Effect.ERROR.getEfName();
        for (Effect name : Effect.values()) {
            if (name.getEfNum() == effectNum) {
                effectName = name.getEfName();
            }
        }
        for (Effect name : Effect.values()) {
            if (name.getEfName().equals(effectName)) {
                return name.getEfName();
            }
        }
        return Effect.ERROR.getEfName();
    }

    public String getType(Player player) {
        int typeNum = plugin.ymlManager.getConfig().getInt("room." + cubeUtil.getCubeNumber(cubeUtil.getCubedPosition(player)) + ".type");
        String typeName = RoomType.ERROR.getName();
        for (RoomType roomType : RoomType.values()) {
            if (roomType.getNum() == typeNum) {
                typeName = roomType.getName();
            }
        }
        for (RoomType type : RoomType.values()) {
            if (type.getName().equals(typeName)) {
                return type.getName();
            }
        }
        return RoomType.ERROR.getName();
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

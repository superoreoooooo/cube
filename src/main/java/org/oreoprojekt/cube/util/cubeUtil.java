package org.oreoprojekt.cube.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.oreoprojekt.cube.CUBE;

import java.util.Random;

public class cubeUtil {

    Boolean timer = false;
    BukkitScheduler scheduler = Bukkit.getScheduler();

    private CUBE plugin;

    public cubeUtil(CUBE main) {
        this.plugin = main;
    }

    public int getCount() {
        return plugin.ymlManager.getConfig().getInt("count");
    } // 방 개수 리턴

    int roomSize = 29;

    double halfRoomSize = (double) roomSize / 2;

    public void printAllRoomLocation(Player player) {
        for (int rn = 0; rn < getCount(); rn++) {
            player.sendMessage(ChatColor.GRAY + "ROOM_NUMBER : " + rn + " / ROOM_X : " + plugin.ymlManager.getConfig().getInt("room." + rn + ".loc." + "locX") + " / ROOM_Z : " + plugin.ymlManager.getConfig().getInt("room." + rn + ".loc." + "locZ"));
        }
    } // 모든 방 리스트 출력

    public String getPlayerFacing(Player player) {
        String s = "null";
        switch (player.getTargetBlock(3).getType().name()) {
            case "CRYING_OBSIDIAN": //East
                s = "EAST";
                break;
            case "OBSIDIAN":        //North
                s = "NORTH";
                break;
            case "COAL_BLOCK":      //West
                s = "WEST";
                break;
            case "BLACKSTONE":      //South
                s = "SOUTH";
                break;
        }
        return s;
    }

    public boolean checkKey(Player player) { //true : 열린문 false : 닫힌문
        int[] playerLoc = getCubedPosition(player);
        String s = getPlayerFacing(player);
        switch (s) {
            case "EAST": // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorE")) {
                    playerLoc[0] = playerLoc[0] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) { // 만약 보고있는 방향의 방이 존재하지 않는다면
                        return false; // error
                    }
                    player.sendMessage("true");
                    return true;
                }
                break;
            case "NORTH": // 북쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorN")) {
                    playerLoc[2] = playerLoc[2] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) { // 만약 보고있는 방향의 방이 존재하지 않는다면
                        return false; // error
                    }
                    player.sendMessage("true");
                    return true;
                }
                break;
            case "WEST": // 서쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorW")) {
                    playerLoc[0] = playerLoc[0] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) { // 만약 보고있는 방향의 방이 존재하지 않는다면
                        return false; // error
                    }
                    player.sendMessage("true");
                    return true;
                }
                break;
            case "SOUTH": // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorS")) {
                    playerLoc[2] = playerLoc[2] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) { // 만약 보고있는 방향의 방이 존재하지 않는다면
                        return false; // error
                    }
                    player.sendMessage("true");
                    return true;
                }
                break;
        }
        return false;
    }

    public void movePlayer(Player player) {
        clearEffect(player);
        int[] playerLoc = getCubedPosition(player);
        String s = getPlayerFacing(player);

        if (getCubeNumber(playerLoc) == -1) {
            player.sendMessage(ChatColor.RED + "ERROR_NOT_IN_ROOM");
            return;
        }

        if (!checkKey(player)) {
            player.sendMessage("열쇠를 소모하여 문을 엽니다.");
            generateCube(player);
        }

        switch (s) {
            case "EAST": // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorE")) {
                    player.getTargetBlock(3).getLocation().add(-2,-2,0).getBlock().setType(Material.EMERALD_BLOCK);
                    player.getTargetBlock(3).getLocation().add(3,-2,0).getBlock().setType(Material.EMERALD_BLOCK);
                    playerLoc[0] = playerLoc[0] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    Bukkit.getConsoleSender().sendMessage(player.getName() + ChatColor.GREEN + " MOVED_TO_ROOM " + ChatColor.YELLOW + getCubeNumber(playerLoc));
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorW", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * roomSize + 1.5), 5, (playerLoc[2] * roomSize + halfRoomSize));
                    player.teleport(pLoc);
                }
                break;
            case "NORTH": // 북쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorN")) {
                    player.getTargetBlock(3).getLocation().add(0,-2,2).getBlock().setType(Material.EMERALD_BLOCK);
                    player.getTargetBlock(3).getLocation().add(0,-2,-3).getBlock().setType(Material.EMERALD_BLOCK);
                    playerLoc[2] = playerLoc[2] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    Bukkit.getConsoleSender().sendMessage(player.getName() + ChatColor.GREEN + " MOVED_TO_ROOM " + ChatColor.YELLOW + getCubeNumber(playerLoc));
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorS", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * roomSize + halfRoomSize), 5, (playerLoc[2] * roomSize + roomSize - 1.5));
                    player.teleport(pLoc);
                }
                break;
            case "WEST": // 서쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorW")) {
                    player.getTargetBlock(3).getLocation().add(2,-2,0).getBlock().setType(Material.EMERALD_BLOCK);
                    player.getTargetBlock(3).getLocation().add(-3,-2,0).getBlock().setType(Material.EMERALD_BLOCK);
                    playerLoc[0] = playerLoc[0] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    Bukkit.getConsoleSender().sendMessage(player.getName() + ChatColor.GREEN + " MOVED_TO_ROOM " + ChatColor.YELLOW + getCubeNumber(playerLoc));
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorE", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * roomSize + roomSize - 1.5), 5, (playerLoc[2] * roomSize + halfRoomSize));
                    player.teleport(pLoc);
                }
                break;
            case "SOUTH": // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorS")) {
                    player.getTargetBlock(3).getLocation().add(0,-2,-2).getBlock().setType(Material.EMERALD_BLOCK);
                    player.getTargetBlock(3).getLocation().add(0,-2,3).getBlock().setType(Material.EMERALD_BLOCK);
                    playerLoc[2] = playerLoc[2] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    Bukkit.getConsoleSender().sendMessage(player.getName() + ChatColor.GREEN + " MOVED_TO_ROOM " + ChatColor.YELLOW + getCubeNumber(playerLoc));
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorN", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * roomSize + halfRoomSize), 5, (playerLoc[2] * roomSize + 1.5));
                    player.teleport(pLoc);
                }
                break;
        }
        giveEffect(player);
    } // 플레이어 이동 -> 이후 알고리즘 수정예정

    public int[] getCubedPosition(Player player) {
        int[] playerPos = new int[3];
        int[] cubePos = new int[3];

        playerPos[0] = (int) player.getLocation().getX();
        playerPos[1] = 10;
        playerPos[2] = (int) player.getLocation().getZ();

        if (playerPos[0] < 0) {
            cubePos[0] = playerPos[0] / roomSize - 1;
        }
        else {
            cubePos[0] = playerPos[0] / roomSize;
        }
        if (playerPos[2] < 0) {
            cubePos[2] = playerPos[2] / roomSize - 1;
        }
        else {
            cubePos[2] = playerPos[2] / roomSize;
        }
        cubePos[1] = playerPos[1];

        return cubePos;
    } // 플레이어 있는 위치를 roomSize으로 나눈 값 리턴

    public int getCubeNumber(int[] playerLoc) {

        for (int roomNumber = 0; roomNumber < getCount(); roomNumber++) {
            if (plugin.ymlManager.getConfig().getInt("room." + roomNumber + ".loc." + "locX") == playerLoc[0] && plugin.ymlManager.getConfig().getInt("room." + roomNumber + ".loc." + "locZ") == playerLoc[2])  {
                return roomNumber;
            }
        }
        return -1;
    } // 플레이어의 위치를 큐브 좌표계로 변환 후 리턴

    public boolean cubeCheck(int[] playerLoc) {
        for (int roomNo = 0; roomNo < getCount(); roomNo++) {
            if (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locX") == playerLoc[0] && plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locZ") == playerLoc[2])  {
                return true;
            }
        }
        return false;
    } // 플레이어 위치에 큐브 있으면 true 없으면 false

    public void checkMain() {
        if (getCount() > 0) return;
        generateMainCube();
    } // 메인 큐브 여부 확인 솔직히 쓸데없음

    public void generateMainCube() {
        plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locX", 0);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locY", 10);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locZ", 0);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorE", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorN", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorW", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorS", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".type", "spawn");
        plugin.ymlManager.getConfig().set("count", getCount() + 1);
        plugin.ymlManager.saveConfig();
        int originX = 0;
        int originZ = 0;
        int originY = 4;
        int exX = 20000;
        int exY = 4;
        int exZ = 20000;

        Location origin = new Location(Bukkit.getWorld("world"), originX, 4, originZ);
        Location ex = new Location(Bukkit.getWorld("world"), exX, 4, exZ);

        for (int x = 0; x < roomSize; x++) {
            for (int z = 0; z < roomSize; z++) {
                for (int y = 0; y < roomSize; y++) {
                    origin.getBlock().setType(ex.getBlock().getType());
                    origin.set(originX + x, originY + y, originZ + z);
                    ex.set(exX + x, exY + y, exZ + z);
                }
            }
        }
    } // 메인 큐브 생성 (0,0)

    public void generateCube(Player player) {
        int[] playerLoc = getCubedPosition(player);
        int originX = 0;
        int originY = 4;
        int originZ = 0;

        int tickLeft = 10;

        int exX = 10000;
        int exY = 4;
        int exZ = 10000;

        switch (getPlayerFacing(player)) {
            case "EAST":
                plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorE", true);

                playerLoc[0] = playerLoc[0] + 1;

                if (cubeCheck(playerLoc)) {
                    //player.sendMessage(ChatColor.RED + "ERROR_ROOM_ALREADY_EXISTS");
                    return;
                }

                Bukkit.getConsoleSender().sendMessage(player.getName() + " : " +  ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_EAST " + ChatColor.WHITE + getCount());
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locX", playerLoc[0]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locY", playerLoc[1]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locZ", playerLoc[2]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorE", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorN", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorW", true);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorS", false);
                break;
            case "NORTH":
                plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorN", true);

                playerLoc[2] = playerLoc[2] - 1;

                if (cubeCheck(playerLoc)) {
                    //player.sendMessage(ChatColor.RED + "ERROR_ROOM_ALREADY_EXISTS");
                    return;
                }

                Bukkit.getConsoleSender().sendMessage(player.getName() + " : " +  ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_NORTH " + ChatColor.WHITE + getCount());
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locX", playerLoc[0]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locY", playerLoc[1]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locZ", playerLoc[2]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorE", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorN", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorW", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorS", true);
                break;
            case "WEST":
                plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorW", true);

                playerLoc[0] = playerLoc[0] - 1;

                if (cubeCheck(playerLoc)) {
                    //player.sendMessage(ChatColor.RED + "ERROR_ROOM_ALREADY_EXISTS");
                    return;
                }

                Bukkit.getConsoleSender().sendMessage(player.getName() + " : " +  ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_WEST " + ChatColor.WHITE + getCount());
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locX", playerLoc[0]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locY", playerLoc[1]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locZ", playerLoc[2]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorE", true);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorN", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorW", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorS", false);
                break;
            case "SOUTH":
                plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorS", true);

                playerLoc[2] = playerLoc[2] + 1;

                if (cubeCheck(playerLoc)) {
                    //player.sendMessage(ChatColor.RED + "ERROR_ROOM_ALREADY_EXISTS");
                    return;
                }

                Bukkit.getConsoleSender().sendMessage(player.getName() + " : " + ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_SOUTH " + ChatColor.WHITE + getCount());
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locX", playerLoc[0]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locY", playerLoc[1]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locZ", playerLoc[2]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorE", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorN", true);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorW", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorS", false);
                break;
        }

        plugin.ymlManager.getConfig().set("room." + getCount() + ".effect", randomEffect());
        plugin.ymlManager.getConfig().set("room." + getCount() + ".type", randomType());
        plugin.ymlManager.getConfig().set("room." + getCount() + ".tickLeft", tickLeft);

        int cnt = getCount();

        plugin.ymlManager.getConfig().set("count", getCount() + 1);

        plugin.ymlManager.saveConfig();

        Location origin = new Location(player.getWorld(), originX, 4, originZ);
        Location ex = new Location(player.getWorld(), exX, 4, exZ);

        originX = (playerLoc[0] * roomSize);
        originZ = (playerLoc[2] * roomSize);

        for (int x = 0; x < roomSize; x++) {
            for (int z = 0; z < roomSize; z++) {
                for (int y = 0; y < roomSize; y++) {
                    origin.getBlock().setType(ex.getBlock().getType());
                    origin.set(originX + x, originY + y, originZ + z);
                    ex.set(exX + x, exY + y, exZ + z);
                }
            }
        }

        origin.getBlock().setType(Material.WHITE_CONCRETE);
        roomTimer(cnt);
    } // 큐브 생성

    public double[] getCubeMidPosition(int roomNo) {
        double[] room = new double[3];
        room[0] = (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locX") * roomSize) + halfRoomSize;
        room[1] = 1;
        room[2] = (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locZ") * roomSize) + halfRoomSize;
        return room;
    } //큐브 중심점 리턴

    public double[] getCubePosition(int roomNo) {
        double[] room = new double[3];
        room[0] = (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locX") * roomSize);
        room[1] = 1;
        room[2] = (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locZ") * roomSize);
        return room;
    } //큐브 시작점 리턴

    public void resetRoom(int roomNo) {
        double[] loc = getCubeMidPosition(roomNo);
        double[] cubePos = getCubePosition(roomNo);
        Location location = new Location(Bukkit.getWorld("world"), loc[0], 7, loc[2]);
        for (Entity entity : location.getWorld().getEntities()) {
            if (entity.getLocation().getX() > cubePos[0]
                    && entity.getLocation().getX() < cubePos[0] + roomSize
                    && entity.getLocation().getZ() > cubePos[2]
                    && entity.getLocation().getZ() < cubePos[2] + roomSize) {
                if (!(entity instanceof Player)) {
                    entity.remove();
                }
            }
        }
        Bukkit.getWorld("world").spawnEntity(location, EntityType.GIANT);
    } // 방 몹 초기화 이후 기능추가예정

    public int randomType() {
        Random random1 = new Random();
        int cubeType = random1.nextInt(1000);
        cubeType += 1;

        int type;

        if (cubeType <= 1) {
            type = 1; // up
        }
        else if (cubeType <= 9) {
            type = 2; // boss
        }
        else if (cubeType <= 100) {
            type = 3; // item
        }
        else if (cubeType <= 200) {
            type = 4; // normal
        }
        else {
            type = 0; // enemy
        }

        return type;
    } // 랜덤한 이펙트

    public int randomEffect() {
        Random random1 = new Random();
        int cubeEffect = random1.nextInt(100);
        cubeEffect += 1;

        int effect;

        if (cubeEffect <= 6) {
            effect = 1;
        }
        else if (cubeEffect <= 12) {
            effect = 2;
        }
        else if (cubeEffect <= 18) {
            effect = 3;
        }
        else if (cubeEffect <= 24) {
            effect = 4;
        }
        else if (cubeEffect <= 30) {
            effect = 5;
        }
        else if (cubeEffect <= 36) {
            effect = 6;
        }
        else if (cubeEffect <= 42) {
            effect = 7;
        }
        else if (cubeEffect <= 48) {
            effect = 8;
        }
        else if (cubeEffect <= 49) {
            effect = 9;
        }
        else if (cubeEffect <= 50){
            effect = 10;
        }
        else {
            effect = 0;
        }

        return effect;
    } // 랜덤한 이펙트

    public void giveEffect(Player player) {
        int effectNumber = plugin.ymlManager.getConfig().getInt("room." + getCubeNumber(getCubedPosition(player)) + ".effect");
        switch (effectNumber) {
            case 0:
                clearEffect(player);
                break;
            case 1:
                player.addPotionEffect(PotionEffectType.SLOW.createEffect(10000,10));
                break;
            case 2:
                player.addPotionEffect(PotionEffectType.SLOW.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.JUMP.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(10000,2));
                break;
            case 3:
                player.addPotionEffect(PotionEffectType.POISON.createEffect(10000,3));
                break;
            case 4:
                player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(10000,1));
                break;
            case 5:
                player.addPotionEffect(PotionEffectType.WITHER.createEffect(10000,5));
                break;
            case 6:
                player.addPotionEffect(PotionEffectType.CONFUSION.createEffect(10000,1));
                break;
            case 7:
                player.setFoodLevel(0);
                break;
            case 8:
                player.addPotionEffect(PotionEffectType.SLOW_DIGGING.createEffect(10000,5));
                break;
            case 9:
                player.addPotionEffect(PotionEffectType.SLOW.createEffect(10000,10));
                player.addPotionEffect(PotionEffectType.SLOW_DIGGING.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.CONFUSION.createEffect(10000,1));
                player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(10000,1));
                player.setFoodLevel(0);
                player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(10000,5));
                break;
            case 10:
                player.addPotionEffect(PotionEffectType.SPEED.createEffect(10000,3));
                player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(10000,2));
                player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.ABSORPTION.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.LUCK.createEffect(10000,1));
                break;
        }
    } // 이펙트 부여

    public void clearEffect(Player player) {
        player.setFoodLevel(20);
        for (PotionEffect e : player.getActivePotionEffects()) {
            player.removePotionEffect(e.getType());
        }
    } // 이펙트 클리어

    public void startTimer() {
        if (timer) {
            return;
        }
        for (int i = 1; i < getCount(); i++) {
            roomTimer(i);
        }
        timer = true;
        plugin.runBoard();
    } // 타이머 시작

    public void stopTimer() {
        timer = false;
        Bukkit.getScheduler().cancelTasks(plugin);
        plugin.ymlManager.saveConfig();
    } // 타이머 멈춤

    public void restartTimer() {
        timer = false;
        Bukkit.getScheduler().cancelTasks(plugin);
        plugin.runBoard();
        plugin.ymlManager.saveConfig();
    } // 타이머 재시작 (스코어보드 타이머는 재시작)

    public void roomTimer(int roomNo) {
        scheduler.runTaskTimer(plugin, new Runnable() {
            int tickLeft = plugin.ymlManager.getConfig().getInt("room." + roomNo + ".tickLeft");
            final int resetTick = 10;//plugin.getConfig().getRoomS("system.resettime");
            @Override
            public void run() {
                if (tickLeft == resetTick) {
                    resetRoom(roomNo);
                    tickLeft--;
                }
                else if (tickLeft > 0) {
                    tickLeft--;
                }
                else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "tick for room " + ChatColor.GRAY + roomNo);
                    tickLeft = resetTick;
                }
                plugin.ymlManager.getConfig().set("room." + roomNo + ".tickLeft", tickLeft);
            }
        }, 0, 20);
    } // 방 타이머 (렉심함, 이후 교체예정)

}
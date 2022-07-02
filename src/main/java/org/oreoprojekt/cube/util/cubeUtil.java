package org.oreoprojekt.cube.util;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.enums.Facing;
import org.oreoprojekt.cube.system.cubeInitial;
import org.oreoprojekt.cube.util.utils.util_Randomizer;

import java.util.HashMap;
import java.util.UUID;

public class cubeUtil {
    private final CUBE plugin;

    public static HashMap<UUID, Integer> OpenCheckerList = new HashMap<>(); //체커 / 방번호
    public static HashMap<UUID, Integer> ClosedCheckerList = new HashMap<>(); //체커 / 방번호
    public static int roomSize = 29;
    public static double halfRoomSize = (double) roomSize / 2;
    public static final World world = Bukkit.getWorld("world");

    Boolean timer = false;

    public cubeUtil(CUBE main) {
        this.plugin = main;
    }

    public int getCount() {
        return plugin.ymlManager.getConfig().getInt("count");
    } // 방 개수 리턴

    public void printAllRoom(Player player) {
        for (int rn = 0; rn < getCount(); rn++) {
            player.sendMessage(ChatColor.GRAY + "ROOM_NUMBER : " + rn + " / ROOM_X : " + plugin.ymlManager.getConfig().getInt("room." + rn + ".loc." + "locX") + " / ROOM_Y : " + plugin.ymlManager.getConfig().getInt("room." + rn + ".loc." + "locY") + " / ROOM_Z : " + plugin.ymlManager.getConfig().getInt("room." + rn + ".loc." + "locZ"));
        }
    } // 모든 방 리스트 출력

    public Facing getPlayerFacing(Player player) {
        switch (player.getTargetBlock(3).getType()) {
            case DIAMOND_BLOCK: //East
                return Facing.EAST;
            case EMERALD_BLOCK: //North
                return Facing.NORTH;
            case GOLD_BLOCK: //West
                return Facing.WEST;
            case NETHERITE_BLOCK: //South
                return Facing.SOUTH;
        }
        return Facing.ERROR;
    }

    public boolean checkKey(Player player, Facing facing) { //true : 열린문 false : 닫힌문
        int[] playerLoc = getCubedPosition(player);
        switch (facing) {
            case EAST: // 동쪽
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorE")) {
                    playerLoc[0] = playerLoc[0] + 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // error
                }
                break;
            case NORTH: // 북쪽
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorN")) {
                    playerLoc[2] = playerLoc[2] - 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // error
                }
                break;
            case SOUTH: // 서쪽
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorW")) {
                    playerLoc[0] = playerLoc[0] - 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // error
                }
                break;
            case WEST: // 동쪽
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorS")) {
                    playerLoc[2] = playerLoc[2] + 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // error
                }
                break;
        }
        return false;
    }

    public boolean checkPass(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getItemMeta().getDisplayName().equals("mastercard") || itemInHand.getItemMeta().getDisplayName().equals("checkcard")) {
            if (checkKey(player, getPlayerFacing(player))) {
                player.sendMessage("문이 열려 있습니다. 들어갑니다.");
                return true;
            } else {
                player.sendMessage("문이 잠겨 있습니다.");
                if (itemInHand.getItemMeta().getDisplayName().equals("mastercard")) {
                    player.sendMessage("마스터 키카드로 문을 열고 들어갑니다.");
                    return true;
                }
                else {
                    if (plugin.pDataYmlManager.getConfig().getInt(player.getName() + ".pass") <= 0) {
                        player.sendMessage("패스를 충전하세요.");
                        return false;
                    }
                    else {
                        player.sendMessage("패스를 사용하여 문을 엽니다.");
                        plugin.pDataYmlManager.getConfig().set(player.getName() + ".pass", plugin.pDataYmlManager.getConfig().getInt(player.getName() + ".pass") - 1);
                        plugin.pDataYmlManager.saveConfig();
                        return true;
                    }
                }
            }
        }

        else return false;
    }

    public void movePlayer(Player player) {

        int[] playerLoc = getCubedPosition(player);
        Facing facing = getPlayerFacing(player);


        if (getCubeNumber(playerLoc) == -1) {
            player.sendMessage(ChatColor.RED + "ERROR_NOT_IN_ROOM");
            return;
        }

        if (checkPass(player)) {
            generateCube(player);
        } else return;

        Location targetBlock = player.getTargetBlock(3).getLocation();
        Location pLoc = player.getLocation();

        targetBlock.add(0,1,0).getBlock().setType(Material.LIME_CONCRETE);

        switch (facing) { //move part
            case EAST: // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorE")) {
                    playerLoc[0] = playerLoc[0] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (checkExist(player, getCubeNumber(playerLoc))) return;
                    targetBlock.add(1,0,0).getBlock().setType(Material.LIME_CONCRETE);
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorW", true);
                    pLoc.set((playerLoc[0] * roomSize + 1.5), (playerLoc[1] * roomSize + 3), (playerLoc[2] * roomSize + halfRoomSize));
                }
                break;
            case NORTH: // 북쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorN")) {
                    playerLoc[2] = playerLoc[2] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (checkExist(player, getCubeNumber(playerLoc))) return;
                    targetBlock.add(0,0,-1).getBlock().setType(Material.LIME_CONCRETE);
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorS", true);
                    pLoc.set((playerLoc[0] * roomSize + halfRoomSize), (playerLoc[1] * roomSize + 3), (playerLoc[2] * roomSize + roomSize - 1.5));
                }
                break;
            case WEST: // 서쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorW")) {
                    playerLoc[0] = playerLoc[0] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (checkExist(player, getCubeNumber(playerLoc))) return;
                    targetBlock.add(-1,0,0).getBlock().setType(Material.LIME_CONCRETE);
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorE", true);
                    pLoc.set((playerLoc[0] * roomSize + roomSize - 1.5), (playerLoc[1] * roomSize + 3), (playerLoc[2] * roomSize + halfRoomSize));
                }
                break;
            case SOUTH: // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorS")) {
                    playerLoc[2] = playerLoc[2] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (checkExist(player, getCubeNumber(playerLoc))) return;
                    targetBlock.add(0,0,1).getBlock().setType(Material.LIME_CONCRETE);
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorN", true);
                    pLoc.set((playerLoc[0] * roomSize + halfRoomSize), (playerLoc[1] * roomSize + 3), (playerLoc[2] * roomSize + 1.5));
                }
                break;
        }
        Bukkit.getConsoleSender().sendMessage(player.getName() + ChatColor.GREEN + " : MOVED_TO_ROOM " + ChatColor.YELLOW + getCubeNumber(playerLoc));
        plugin.ymlManager.saveConfig();
        player.teleport(pLoc);
        clearEffect(player);
        giveEffect(player);
    } // 플레이어 이동 -> 이후 알고리즘 수정예정

    public boolean checkExist(Player player,int cubeNum) {
        if (cubeNum == -1) {
            player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
            return true; // error
        }
        return false;
    }

    public int[] getCubedPosition(Player player) {
        int[] playerPos = new int[3];
        int[] cubePos = new int[3];

        playerPos[0] = (int) player.getLocation().getX();
        playerPos[1] = (int) player.getLocation().getY();
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
        cubePos[1] = playerPos[1] / roomSize;

        return cubePos;
    } // 플레이어 있는 위치를 큐브 위치로 환산해서 리턴

    public int getCubeNumber(int[] playerLoc) {

        for (int roomNumber = 0; roomNumber < getCount(); roomNumber++) {
            if (plugin.ymlManager.getConfig().getInt("room." + roomNumber + ".loc." + "locX") == playerLoc[0] && plugin.ymlManager.getConfig().getInt("room." + roomNumber + ".loc." + "locZ") == playerLoc[2])  {
                return roomNumber;
            }
        }
        return -1;
    } // 플레이어 있는 위치의 큐브 번호를 리턴 위치가 큐브 밖이면 -1 리턴

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
        plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locY", 0);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locZ", 0);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorE", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorN", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorW", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorS", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".type", "spawn");
        plugin.ymlManager.getConfig().set("room." + getCount() + ".effect", 8);
        plugin.ymlManager.getConfig().set("count", getCount() + 1);
        plugin.ymlManager.saveConfig();
        int originX = 0;
        int originZ = 0;
        int originY = 0;
        int exX = 1000000;
        int exY = 0;
        int exZ = 1000000;

        Location origin = new Location(Bukkit.getWorld("world"), originX, originY, originZ);
        Location ex = new Location(Bukkit.getWorld("world"), exX, exY, exZ);

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
        int originY = 0;
        int originZ = 0;

        int exX = 1000000;
        int exY = 0;
        int exZ = 1000029;

        switch (getPlayerFacing(player)) {
            case EAST:
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
            case NORTH:
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
            case WEST:
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
            case SOUTH:
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

        plugin.ymlManager.getConfig().set("room." + getCount() + ".effect", util_Randomizer.random(cubeInitial.effectList));
        plugin.ymlManager.getConfig().set("room." + getCount() + ".type", util_Randomizer.random(cubeInitial.roomType));

        plugin.ymlManager.getConfig().set("count", getCount() + 1);

        plugin.ymlManager.saveConfig();

        Location origin = new Location(player.getWorld(), originX, originY, originZ);
        Location ex = new Location(player.getWorld(), exX, exY, exZ);

        originX = (playerLoc[0] * roomSize);
        originY = (playerLoc[1] * roomSize);
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
        //roomTimer(cnt);
    } // 큐브 생성

    public void giveEffect(Player player) {
        cubeInitial.initialize();
        int effectNumber = plugin.ymlManager.getConfig().getInt("room." + getCubeNumber(getCubedPosition(player)) + ".effect");
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        switch (effectNumber) {
            case 0:
                player.addPotionEffect(PotionEffectType.SLOW.createEffect(10000,10));
                break;
            case 1:
                player.addPotionEffect(PotionEffectType.SLOW.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.JUMP.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(10000,2));
                break;
            case 2:
                player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(10000,1));
                break;
            case 3:
                player.addPotionEffect(PotionEffectType.CONFUSION.createEffect(10000,1));
                break;
            case 4:
                player.setFoodLevel(0);
                break;
            case 5:
                player.addPotionEffect(PotionEffectType.SLOW_DIGGING.createEffect(10000,5));
                break;
            case 6:
                player.addPotionEffect(PotionEffectType.SLOW.createEffect(10000,10));
                player.addPotionEffect(PotionEffectType.SLOW_DIGGING.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.CONFUSION.createEffect(10000,1));
                player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(10000,1));
                player.setFoodLevel(0);
                player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(10000,5));
                break;
            case 7:
                player.addPotionEffect(PotionEffectType.SPEED.createEffect(10000,3));
                player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(10000,2));
                player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.ABSORPTION.createEffect(10000,5));
                player.addPotionEffect(PotionEffectType.LUCK.createEffect(10000,1));
                break;
            case 8:
                clearEffect(player);
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
}
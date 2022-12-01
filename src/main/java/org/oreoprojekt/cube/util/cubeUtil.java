package org.oreoprojekt.cube.util;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.enums.Facing;
import org.oreoprojekt.cube.system.cubeInitial;
import org.oreoprojekt.cube.util.utils.randomizer;

import java.util.HashMap;
import java.util.UUID;

public class cubeUtil {
    private final CUBE plugin;

    public static HashMap<UUID, Integer> OpenCheckerList = new HashMap<>();                 //체커 | 방번호
    public static HashMap<UUID, Integer> ClosedCheckerList = new HashMap<>();               //체커 | 방번호
    public static int cubeSize = 29;
    public static double halfRoomSize = (double) cubeSize / 2;
    public static final World world = Bukkit.getWorld("world");

    private org.oreoprojekt.cube.util.utils.checker checker;

    Boolean timer = false;

    public cubeUtil(CUBE main) {
        this.plugin = main;
    }

    public int getCount() {
        return plugin.ymlManager.getConfig().getInt("total");
    }   // 방 개수 리턴

    public int getLvlCount(int level) {
        return plugin.ymlManager.getConfig().getInt("level." + level + ".count");
    }

    public int getLevels() {
        return plugin.ymlManager.getConfig().getInt("level." + "levelCount");
    }

    public void printPlayerLoc(Player player) {
        int[] pLoc = getCubedPosition(player);
        player.sendMessage(ChatColor.GREEN + "Loc : x " + pLoc[0] + " y " + pLoc[1] + " z " + pLoc[2] + " level " + getLevel(player) + " cube " + getCubeNumber(pLoc));
    }

    public Facing getPlayerFacing(Player player) {
        switch (player.getTargetBlock(3).getType()) {
            case DIAMOND_BLOCK:                 //East
                return Facing.EAST;
            case EMERALD_BLOCK:                 //North
                return Facing.NORTH;
            case GOLD_BLOCK:                    //West
                return Facing.WEST;
            case NETHERITE_BLOCK:               //South
                return Facing.SOUTH;
        }
        return Facing.ERROR;
    }
    
    public int getLevel(Player player) {
        int[] pLoc = getCubedPosition(player);
        return pLoc[1];
    }

    public boolean checkKey(Player player, Facing facing) { //true : 열린문 false : 닫힌문
        int[] playerLoc = getCubedPosition(player);
        switch (facing) {
            case EAST: // 동쪽
                if (plugin.ymlManager.getConfig().getBoolean("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorE")) {
                    playerLoc[0] = playerLoc[0] + 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // 에러체킹
                }
                break;
            case NORTH: // 북쪽
                if (plugin.ymlManager.getConfig().getBoolean("level." + getLevel(player) + ".cube." +  getCubeNumber(playerLoc) + ".door." + "doorN")) {
                    playerLoc[2] = playerLoc[2] - 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // 에러체킹
                }
                break;
            case WEST: // 서쪽
                if (plugin.ymlManager.getConfig().getBoolean("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorW")) {
                    playerLoc[0] = playerLoc[0] - 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // 에러체킹
                }
                break;
            case SOUTH: // 동쪽
                if (plugin.ymlManager.getConfig().getBoolean("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorS")) {
                    playerLoc[2] = playerLoc[2] + 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // 에러체킹
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
                if (plugin.ymlManager.getConfig().getBoolean("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorE")) {
                    playerLoc[0] = playerLoc[0] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (checkExist(player, getLevel(player), getCubeNumber(playerLoc))) return;
                    targetBlock.add(1,0,0).getBlock().setType(Material.LIME_CONCRETE);
                    plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorW", true);
                    pLoc.set((playerLoc[0] * cubeSize + 1.5), (playerLoc[1] * cubeSize + 3), (playerLoc[2] * cubeSize + halfRoomSize));
                }
                break;
            case NORTH: // 북쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorN")) {
                    playerLoc[2] = playerLoc[2] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (checkExist(player, getLevel(player), getCubeNumber(playerLoc))) return;
                    targetBlock.add(0,0,-1).getBlock().setType(Material.LIME_CONCRETE);
                    plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorS", true);
                    pLoc.set((playerLoc[0] * cubeSize + halfRoomSize), (playerLoc[1] * cubeSize + 3), (playerLoc[2] * cubeSize + cubeSize - 1.5));
                }
                break;
            case WEST: // 서쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorW")) {
                    playerLoc[0] = playerLoc[0] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (checkExist(player, getLevel(player), getCubeNumber(playerLoc))) return;
                    targetBlock.add(-1,0,0).getBlock().setType(Material.LIME_CONCRETE);
                    plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorE", true);
                    pLoc.set((playerLoc[0] * cubeSize + cubeSize - 1.5), (playerLoc[1] * cubeSize + 3), (playerLoc[2] * cubeSize + halfRoomSize));
                }
                break;
            case SOUTH: // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorS")) {
                    playerLoc[2] = playerLoc[2] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (checkExist(player, getLevel(player), getCubeNumber(playerLoc))) return;
                    targetBlock.add(0,0,1).getBlock().setType(Material.LIME_CONCRETE);
                    plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorN", true);
                    pLoc.set((playerLoc[0] * cubeSize + halfRoomSize), (playerLoc[1] * cubeSize + 3), (playerLoc[2] * cubeSize + 1.5));
                }
                break;
        }
        Bukkit.getConsoleSender().sendMessage(player.getName() + ChatColor.GREEN + " : MOVED_TO_ROOM : " + ChatColor.YELLOW + "L" + getLevel(player) + " R" + getCubeNumber(playerLoc));
        plugin.ymlManager.saveConfig();
        player.teleport(pLoc);
        clearEffect(player);
        giveEffect(player);
    } // 플레이어 이동 -> 이후 알고리즘 수정예정

    public boolean checkExist(Player player, int level, int cubeNum) {
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
            cubePos[0] = playerPos[0] / cubeSize - 1;
        }
        else {
            cubePos[0] = playerPos[0] / cubeSize;
        }
        if (playerPos[2] < 0) {
            cubePos[2] = playerPos[2] / cubeSize - 1;
        }
        else {
            cubePos[2] = playerPos[2] / cubeSize;
        }
        cubePos[1] = playerPos[1] / cubeSize;

        return cubePos;
    } // 플레이어 있는 위치를 큐브 위치로 환산해서 리턴


    public int getCubeNumber(int[] playerLoc) {
        for (int cubeNumber = 0; cubeNumber < getLvlCount(playerLoc[1]); cubeNumber++) {
            if (plugin.ymlManager.getConfig().getInt("level." + playerLoc[1] + ".cube." + cubeNumber + ".loc." + "locX") == playerLoc[0] &&
                    plugin.ymlManager.getConfig().getInt("level." + playerLoc[1] + ".cube." + cubeNumber + ".loc." + "locY") == playerLoc[1] &&
                    plugin.ymlManager.getConfig().getInt("level." + playerLoc[1] + ".cube." + cubeNumber + ".loc." + "locZ") == playerLoc[2])  {
                return cubeNumber;
            }
        }
        return -1;
    } // 플레이어 있는 위치의 큐브 번호를 리턴 위치가 큐브 밖이면 -1 리턴

    public boolean cubeCheck(int[] playerLoc) {
        for (int cubeNo = 0; cubeNo < getLvlCount(playerLoc[1]); cubeNo++) {
            if (plugin.ymlManager.getConfig().getInt("level." + playerLoc[1] + ".cube." + cubeNo + ".loc." + "locX") == playerLoc[0] &&
                    plugin.ymlManager.getConfig().getInt("level." + playerLoc[1] + ".cube." + cubeNo + ".loc." + "locY") == playerLoc[1] &&
                    plugin.ymlManager.getConfig().getInt("level." + playerLoc[1] + ".cube." + cubeNo + ".loc." + "locZ") == playerLoc[2])  {
                return true;
            }
        }
        return false;
    } // 플레이어 위치에 큐브 있으면 true 없으면 false

    public void checkMain() {
        plugin.ymlManager.saveConfig();
        if (getCount() > 0) return;
        for (int i = 0; i < 7; i++) {
            generateMainCube(i);
        }
    } // 메인 큐브 여부 확인 솔직히 쓸데없음

    public void generateMainCube(int level) {
        int ax = -1;
        int az = -1;
        for (int c = 0; c < 9; c++) {
            plugin.ymlManager.getConfig().set("level." + level + ".cube." + c + ".loc." + "locX", ax);
            plugin.ymlManager.getConfig().set("level." + level + ".cube." + c + ".loc." + "locY", level);
            plugin.ymlManager.getConfig().set("level." + level + ".cube." + c + ".loc." + "locZ", az);
            plugin.ymlManager.getConfig().set("level." + level + ".cube." + c + ".door." + "doorE", false);
            plugin.ymlManager.getConfig().set("level." + level + ".cube." + c + ".door." + "doorN", false);
            plugin.ymlManager.getConfig().set("level." + level + ".cube." + c + ".door." + "doorW", false);
            plugin.ymlManager.getConfig().set("level." + level + ".cube." + c + ".door." + "doorS", false);
            plugin.ymlManager.getConfig().set("level." + level + ".cube." + c + ".type", -2);
            plugin.ymlManager.getConfig().set("level." + level + ".cube." + c + ".effect", 8);
            plugin.ymlManager.getConfig().set("level." + level + ".count", getLvlCount(level) + 1);
            plugin.ymlManager.getConfig().set("total", getCount() + 1);
            plugin.ymlManager.getConfig().set("level." + "levelCount", getLevels() + 1);
            plugin.ymlManager.saveConfig();
            az += 1;
            if (az == 2) {
                ax += 1;
                az = -1;
            }
        }
        int originX = -29;
        int originZ = -29;
        int originY = level * cubeSize;
        int exX = 999971;
        int exY = 0;
        int exZ = 999971;

        Location origin = new Location(Bukkit.getWorld("world"), originX, originY, originZ);
        Location ex = new Location(Bukkit.getWorld("world"), exX, exY, exZ);

        for (int x = 0; x < cubeSize * 3; x++) {
            for (int z = 0; z < cubeSize * 3; z++) {
                for (int y = 0; y < cubeSize; y++) {
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
        int exZ = 1000000 + 2 * cubeSize;

        plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".door." + "doorE", false);
        plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".door." + "doorN", false);
        plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".door." + "doorW", false);
        plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".door." + "doorS", false);

        switch (getPlayerFacing(player)) {
            case EAST:
                plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorE", true);
                playerLoc[0] = playerLoc[0] + 1;
                if (cubeCheck(playerLoc)) return;
                Bukkit.getConsoleSender().sendMessage(player.getName() + " : " +  ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_EAST " + ChatColor.WHITE + getCount());
                plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".door." + "doorW", true);
                break;
            case NORTH:
                plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorN", true);
                playerLoc[2] = playerLoc[2] - 1;
                if (cubeCheck(playerLoc)) return;
                Bukkit.getConsoleSender().sendMessage(player.getName() + " : " +  ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_NORTH " + ChatColor.WHITE + getLvlCount(getLevel(player)));
                plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".door." + "doorS", true);
                break;
            case WEST:
                plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorW", true);
                playerLoc[0] = playerLoc[0] - 1;
                if (cubeCheck(playerLoc)) return;
                Bukkit.getConsoleSender().sendMessage(player.getName() + " : " +  ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_WEST " + ChatColor.WHITE + getLvlCount(getLevel(player)));
                plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".door." + "doorE", true);
                break;
            case SOUTH:
                plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getCubeNumber(playerLoc) + ".door." + "doorS", true);
                playerLoc[2] = playerLoc[2] + 1;
                if (cubeCheck(playerLoc)) return;
                Bukkit.getConsoleSender().sendMessage(player.getName() + " : " + ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_SOUTH " + ChatColor.WHITE + getLvlCount(getLevel(player)));
                plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".door." + "doorN", true);
                break;
            default:
                return;
        }

        plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".loc." + "locX", playerLoc[0]);
        plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".loc." + "locY", playerLoc[1]);
        plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".loc." + "locZ", playerLoc[2]);

        plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".effect", randomizer.random(cubeInitial.effectList));
        plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".cube." + getLvlCount(getLevel(player)) + ".type", randomizer.random(cubeInitial.cubeType));

        plugin.ymlManager.getConfig().set("level." + getLevel(player) + ".count", getLvlCount(getLevel(player)) + 1);
        plugin.ymlManager.getConfig().set("count", getCount() + 1);

        plugin.ymlManager.saveConfig();

        originX = (playerLoc[0] * cubeSize);
        originY = (playerLoc[1] * cubeSize);
        originZ = (playerLoc[2] * cubeSize);

        Location origin = new Location(player.getWorld(), originX, originY, originZ);
        Location ex = new Location(player.getWorld(), exX, exY, exZ);

        for (int x = 0; x < cubeSize; x++) {
            for (int z = 0; z < cubeSize; z++) {
                for (int y = 0; y < cubeSize; y++) {
                    origin.getBlock().setType(ex.getBlock().getType());
                    origin.set(originX + x, originY + y, originZ + z);
                    ex.set(exX + x, exY + y, exZ + z);
                }
            }
        }
    } // 큐브 생성

    public void giveEffect(Player player) {
        cubeInitial.initialize();
        int effectNumber = plugin.ymlManager.getConfig().getInt("level." + getLevel(player) + ".cube." + getCubeNumber(getCubedPosition(player)) + ".effect");
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        switch (effectNumber) {
            case -1:
                clearEffect(player);
                player.sendMessage("ERROR");
                break;
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
            default:
                clearEffect(player);
                player.sendMessage("spawn");
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
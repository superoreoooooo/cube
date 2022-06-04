package org.oreoprojekt.cube.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.system.cubeInitial;

import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

    public int roomSize = 29;

    public double halfRoomSize = (double) roomSize / 2;

    public void printAllRoomLocation(Player player) {
        for (int rn = 0; rn < getCount(); rn++) {
            player.sendMessage(ChatColor.GRAY + "ROOM_NUMBER : " + rn + " / ROOM_X : " + plugin.ymlManager.getConfig().getInt("room." + rn + ".loc." + "locX") + " / ROOM_Y : " + plugin.ymlManager.getConfig().getInt("room." + rn + ".loc." + "locY") + " / ROOM_Z : " + plugin.ymlManager.getConfig().getInt("room." + rn + ".loc." + "locZ"));
        }
    } // 모든 방 리스트 출력

    public String getPlayerFacing(Player player) {
        String s = "null";
        switch (player.getTargetBlock(3).getType().name()) {
            case "DIAMOND_BLOCK": //East
                s = "EAST";
                break;
            case "EMERALD_BLOCK":        //North
                s = "NORTH";
                break;
            case "GOLD_BLOCK":      //West
                s = "WEST";
                break;
            case "NETHERITE_BLOCK":      //South
                s = "SOUTH";
                break;
        }
        return s;
    }

    public boolean checkKey(Player player, String facing) { //true : 열린문 false : 닫힌문
        int[] playerLoc = getCubedPosition(player);
        switch (facing) {
            case "EAST": // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorE")) {
                    playerLoc[0] = playerLoc[0] + 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // error
                }
                break;
            case "NORTH": // 북쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorN")) {
                    playerLoc[2] = playerLoc[2] - 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // error
                }
                break;
            case "WEST": // 서쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorW")) {
                    playerLoc[0] = playerLoc[0] - 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // error
                }
                break;
            case "SOUTH": // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorS")) {
                    playerLoc[2] = playerLoc[2] + 1; // 보고있는 방향의 방 위치값을 얻음
                    return getCubeNumber(playerLoc) != -1; // error
                }
                break;
        }
        return false;
    }

    public HashMap<UUID, Integer> OpenCheckerList = new HashMap<>(); //체커 / 방번호
    public HashMap<UUID, Integer> ClosedCheckerList = new HashMap<>(); //체커 / 방번호
    public List<Player> checkerTimerList = new ArrayList<>();

    public int checkerTask;

    public void checkerTimer(Player player) {
        if (!checkerTimerList.contains(player)) checkerTimerList.add(player);
        else return;
        checkerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                killChecker(player);
                spawnChecker(player);
            }
        }, 0, 50);
    }

    public void killChecker(Player player) {
        int cnt = 0;
        int[] playerLoc = getCubedPosition(player);
        for (int i = 0; i < getCount(); i++) {
            if (i != getCubeNumber(playerLoc)) {
                for (ArmorStand checker : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
                    if (checker.hasMetadata("pos")) {
                        String[] metaData = checker.getMetadata("pos").get(0).asString().split("\\.");
                        player.sendMessage("room " + metaData[0] + " face " + metaData[1] + " O/C " + metaData[2]);
                        if (!metaData.equals(Integer.toString(i))) {
                            if (metaData[1].equals("O")) OpenCheckerList.remove(checker.getUniqueId());
                            else ClosedCheckerList.remove(checker.getUniqueId());
                            checker.remove();
                            cnt++;
                        }
                    }
                }
            }
        }
        /**
        int[] playerLoc = getCubedPosition(player);
        int cubeNo = getCubeNumber(playerLoc);
        int cubeNo2 = -2;
        int cnt = 0;
        //if (true) return; //난 평화주의자다! > disabled
        for (Player p : Bukkit.getOnlinePlayers()) {
            cubeNo2 = getCubeNumber(getCubedPosition(p));
            if (cubeNo2 == cubeNo) return;
        }
        for (ArmorStand checker : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
            if (checker.hasMetadata("pos")) {
                String[] metaData = checker.getMetadata("pos").get(0).asString().split("\\.");
                player.sendMessage(metaData[0] + metaData[1] + metaData[2]);
                if (!metaData[0].equals(Integer.toString(cubeNo))) {
                    if (metaData[1].equals("O")) OpenCheckerList.remove(checker.getUniqueId());
                    else ClosedCheckerList.remove(checker.getUniqueId());
                    checker.remove();
                    cnt++;
                }
            }
        }**/

        /**
        for (UUID uuid : OpenCheckerList.keySet()) {
            if (OpenCheckerList.get(uuid).equals(getCubeNumber(playerLoc))) {
                if (!OpenCheckerList.containsKey(uuid)) continue;
                OpenCheckerList.remove(uuid);
                player.sendMessage(uuid.toString());
                cnt++;
            }
        }
        for (UUID uuid : ClosedCheckerList.keySet()) {
            if (ClosedCheckerList.get(uuid).equals(getCubeNumber(playerLoc))) {
                if (!ClosedCheckerList.containsKey(uuid)) continue;
                ClosedCheckerList.remove(uuid);
                player.sendMessage(uuid.toString());
                cnt++;
            }
        }**/
        player.sendMessage("Killed " + cnt + " checkers!");
    }

    public void spawnChecker(Player player) {
        int[] playerLoc = getCubedPosition(player);
        Location[] locations = new Location[4];
        locations[0] = new Location(player.getWorld(), playerLoc[0] * roomSize + roomSize -2, playerLoc[1] * roomSize + 2, playerLoc[2] * roomSize + halfRoomSize); //EAST
        locations[1] = new Location(player.getWorld(), playerLoc[0] * roomSize + halfRoomSize, playerLoc[1] * roomSize + 2, playerLoc[2] * roomSize + 2); //NORTH
        locations[2] = new Location(player.getWorld(), playerLoc[0] * roomSize + 2, playerLoc[1] * roomSize + 2, playerLoc[2] * roomSize + halfRoomSize); //WEST
        locations[3] = new Location(player.getWorld(), playerLoc[0] * roomSize + halfRoomSize, playerLoc[1] * roomSize + 2, playerLoc[2] * roomSize + roomSize - 2); //SOUTH

        for (int c = 0; c < 4; c++) {
            player.sendMessage(ChatColor.YELLOW + "Spawning " + c + " checker");
            switch (c) {
                case 0:
                    if (checkKey(player, "EAST")) spawnOpenChecker(player, locations[0], getCubeNumber(playerLoc) + ".E.O");
                    else spawnClosedChecker(player, locations[0], getCubeNumber(playerLoc) + ".E.C");
                    break;
                case 1:
                    if (checkKey(player, "NORTH")) spawnOpenChecker(player, locations[1], getCubeNumber(playerLoc) + ".N.O");
                    else spawnClosedChecker(player, locations[1], getCubeNumber(playerLoc) + ".N.C");
                    break;
                case 2:
                    if (checkKey(player, "WEST")) spawnOpenChecker(player, locations[2], getCubeNumber(playerLoc) + ".W.O");
                    else spawnClosedChecker(player, locations[2], getCubeNumber(playerLoc) + ".W.C");
                    break;
                case 3:
                    if (checkKey(player, "SOUTH")) spawnOpenChecker(player, locations[3], getCubeNumber(playerLoc) + ".S.O");
                    else spawnClosedChecker(player, locations[3], getCubeNumber(playerLoc) + ".S.C");
                    break;
            }
        }
    }

    public void spawnOpenChecker(Player player, Location location, String checkerName) {
        ArmorStand checkerOpen = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        checkerOpen.setCustomName(ChatColor.GREEN + "OPEN");
        checkerOpen.setGravity(false);
        checkerOpen.setCanPickupItems(false);
        checkerOpen.setVisible(false);
        checkerOpen.setCanMove(false);
        checkerOpen.setMarker(true);
        checkerOpen.setCustomNameVisible(true);
        checkerOpen.setMetadata("pos", new FixedMetadataValue(plugin, checkerName));
        OpenCheckerList.put(checkerOpen.getUniqueId(), getCubeNumber(getCubedPosition(player)));
    }

    public void spawnClosedChecker(Player player, Location location, String checkerName) {
        ArmorStand checkerClosed = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        checkerClosed.setCustomName(ChatColor.RED + "CLOSED");
        checkerClosed.setGravity(false);
        checkerClosed.setCanPickupItems(false);
        checkerClosed.setVisible(false);
        checkerClosed.setCanMove(false);
        checkerClosed.setMarker(true);
        checkerClosed.setCustomNameVisible(true);
        checkerClosed.setMetadata("pos", new FixedMetadataValue(plugin, checkerName));
        ClosedCheckerList.put(checkerClosed.getUniqueId(), getCubeNumber(getCubedPosition(player)));
    }

    public void countChecker(Player player) {
        if (OpenCheckerList.size() == 0 && ClosedCheckerList.size() == 0) {
            player.sendMessage("NULL");
            return;
        }
        for (UUID uuid : OpenCheckerList.keySet()) {
            if (Bukkit.getEntity(uuid) == null) return;
            player.sendMessage("O : " + uuid + OpenCheckerList.get(uuid) + " DATA : " + Bukkit.getEntity(uuid).getMetadata("pos").get(0).asString());
        }
        for (UUID uuid : ClosedCheckerList.keySet()) {
            if (Bukkit.getEntity(uuid) == null) return;
            player.sendMessage("C : " + uuid + OpenCheckerList.get(uuid) + " DATA : " + Bukkit.getEntity(uuid).getMetadata("pos").get(0).asString());
        }
    }

    public int countCheckers() {
        int cnt = 0;
        if (OpenCheckerList.size() == 0 && ClosedCheckerList.size() == 0) {
            return -1;
        }
        for (UUID uuid : OpenCheckerList.keySet()) {
            if (Bukkit.getEntity(uuid) != null) cnt++;
        }
        for (UUID uuid : ClosedCheckerList.keySet()) {
            if (Bukkit.getEntity(uuid) != null) cnt++;
        }
        return cnt;
    }

    public void movePlayer(Player player) {

        int[] playerLoc = getCubedPosition(player);
        String s = getPlayerFacing(player);
        boolean b = true;

        if (getCubeNumber(playerLoc) == -1) {
            player.sendMessage(ChatColor.RED + "ERROR_NOT_IN_ROOM");
            return;
        }
        Location targetBlock = player.getTargetBlock(3).getLocation();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getItemMeta().getDisplayName().equals("mastercard") || itemInHand.getItemMeta().getDisplayName().equals("checkcard")) {
            if (checkKey(player, getPlayerFacing(player))) {
                player.sendMessage("문이 열려 있습니다. 들어갑니다.");
            } else {
                player.sendMessage("문이 잠겨 있습니다.");
                if (itemInHand.getItemMeta().getDisplayName().equals("mastercard")) {
                    player.sendMessage("마스터 키카드로 문을 열고 들어갑니다.");
                    generateCube(player);
                }
                else {
                    if (plugin.pDataYmlManager.getConfig().getInt(player.getName() + ".pass") <= 0) {
                        player.sendMessage("패스를 충전하세요.");
                        b = false;
                        return;
                    }
                    else {
                        player.sendMessage("패스를 사용하여 문을 엽니다.");
                        plugin.pDataYmlManager.getConfig().set(player.getName() + ".pass", plugin.pDataYmlManager.getConfig().getInt(player.getName() + ".pass") - 1);
                        plugin.pDataYmlManager.saveConfig();
                        generateCube(player);
                    }
                }
            }
        }

        else return;
        if (!b) return;

        switch (s) { //move part
            case "EAST": // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorE")) {
                    playerLoc[0] = playerLoc[0] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    targetBlock.add(0,1,0).getBlock().setType(Material.LIME_CONCRETE);
                    targetBlock.add(1,0,0).getBlock().setType(Material.LIME_CONCRETE);
                    Bukkit.getConsoleSender().sendMessage(player.getName() + ChatColor.GREEN + " : MOVED_TO_ROOM " + ChatColor.YELLOW + getCubeNumber(playerLoc));
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorW", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * roomSize + 1.5), (playerLoc[1] * roomSize + 3), (playerLoc[2] * roomSize + halfRoomSize));
                    player.teleport(pLoc);
                }
                break;
            case "NORTH": // 북쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorN")) {
                    playerLoc[2] = playerLoc[2] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    targetBlock.add(0,1,0).getBlock().setType(Material.LIME_CONCRETE);
                    targetBlock.add(0,0,-1).getBlock().setType(Material.LIME_CONCRETE);
                    Bukkit.getConsoleSender().sendMessage(player.getName() + ChatColor.GREEN + " : MOVED_TO_ROOM " + ChatColor.YELLOW + getCubeNumber(playerLoc));
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorS", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * roomSize + halfRoomSize), (playerLoc[1] * roomSize + 3), (playerLoc[2] * roomSize + roomSize - 1.5));
                    player.teleport(pLoc);
                }
                break;
            case "WEST": // 서쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorW")) {
                    playerLoc[0] = playerLoc[0] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    targetBlock.add(0,1,0).getBlock().setType(Material.LIME_CONCRETE);
                    targetBlock.add(-1,0,0).getBlock().setType(Material.LIME_CONCRETE);
                    Bukkit.getConsoleSender().sendMessage(player.getName() + ChatColor.GREEN + " : MOVED_TO_ROOM " + ChatColor.YELLOW + getCubeNumber(playerLoc));
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorE", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * roomSize + roomSize - 1.5), (playerLoc[1] * roomSize + 3), (playerLoc[2] * roomSize + halfRoomSize));
                    player.teleport(pLoc);
                }
                break;
            case "SOUTH": // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorS")) {
                    playerLoc[2] = playerLoc[2] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    targetBlock.add(0,1,0).getBlock().setType(Material.LIME_CONCRETE);
                    targetBlock.add(0,0,1).getBlock().setType(Material.LIME_CONCRETE);
                    Bukkit.getConsoleSender().sendMessage(player.getName() + ChatColor.GREEN + " : MOVED_TO_ROOM " + ChatColor.YELLOW + getCubeNumber(playerLoc));
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorN", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * roomSize + halfRoomSize), (playerLoc[1] * roomSize + 3), (playerLoc[2] * roomSize + 1.5));
                    player.teleport(pLoc);
                }
                break;
        }
        clearEffect(player);
        giveEffect(player);
    } // 플레이어 이동 -> 이후 알고리즘 수정예정

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

        int tickLeft = 10;

        int exX = 1000000;
        int exY = 0;
        int exZ = 1000029;

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

        plugin.ymlManager.getConfig().set("room." + getCount() + ".effect", cubeRandomPicker.random(cubeInitial.effectList));
        plugin.ymlManager.getConfig().set("room." + getCount() + ".type", cubeRandomPicker.random(cubeInitial.roomType));
        //plugin.ymlManager.getConfig().set("room." + getCount() + ".tickLeft", tickLeft);

        int cnt = getCount();

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

    public double[] getCubeMidPosition(int roomNo) {
        double[] room = new double[3];
        room[0] = (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locX") * roomSize) + halfRoomSize;
        room[1] = (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locY") * roomSize) + 2;
        room[2] = (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locZ") * roomSize) + halfRoomSize;
        return room;
    } //큐브 중심점 리턴

    public double[] getCubePosition(int roomNo) {
        double[] room = new double[3];
        room[0] = (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locX") * roomSize);
        room[1] = (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locY") * roomSize) + 2;
        room[2] = (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locZ") * roomSize);
        return room;
    } //큐브 시작점 리턴

    public int[] getPlayerCubeLocation(Player player) {
        int[] position = new int[3];
        position[0] = ((int) Math.round(player.getLocation().getX())) % roomSize;
        position[1] = ((int) Math.round(player.getLocation().getY())) % roomSize;
        position[2] = ((int) Math.round(player.getLocation().getZ())) % roomSize;
        return position;
    } //큐브 내에 있는 플레이어의 위치값 (정수형)

    /**
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
    } // 방 몹 초기화 이후 기능추가예정 //제거**/

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
                /**
            case 3:
                player.addPotionEffect(PotionEffectType.POISON.createEffect(10000,3));
                break;
                 **/
            case 2:
                player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(10000,1));
                break;
                /**
            case 5:
                player.addPotionEffect(PotionEffectType.WITHER.createEffect(10000,5));
                break;
                 **/
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
        /**
        for (int i = 1; i < getCount(); i++) {
            roomTimer(i);
        }**/
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

    /**
    public void roomTimer(int roomNo) {
        scheduler.runTaskTimer(plugin, new Runnable() {
            int tickLeft = plugin.ymlManager.getConfig().getInt("room." + roomNo + ".tickLeft");
            final int resetTick = 10; //plugin.getConfig().getRoomS("system.resettime");
            //@Override
            public void run() {
                if (tickLeft == resetTick) {
                    resetRoom(roomNo);
                    tickLeft--;
                }
                else if (tickLeft > 0) {
                    tickLeft--;
                }
                else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Ticked Room : " + ChatColor.GRAY + roomNo);
                    tickLeft = resetTick;
                }
                plugin.ymlManager.getConfig().set("room." + roomNo + ".tickLeft", tickLeft);
            }
        }, 0, 20);
    } // 방 타이머 (렉심함, 이후 교체예정) //제거 **/

    private void setDoor(int xWorld, int yWorld, int zWorld, Material eDoorType, BlockFace eFace) //안씀
    {
        Block bottom = Bukkit.getWorld("world").getBlockAt(xWorld, yWorld, zWorld);
        Block top = bottom.getRelative(BlockFace.DOWN);
        bottom.setType(eDoorType, false);
        top.setType(eDoorType, false);

        Door d1 = (Door) bottom.getBlockData();
        Door d2 = (Door) top.getBlockData();
        d1.setHalf(Bisected.Half.BOTTOM);
        d2.setHalf(Bisected.Half.BOTTOM);
        d1.setFacing(eFace);
        d2.setFacing(eFace);

        bottom.setBlockData(d1);
        top.setBlockData(d2);
    }
}
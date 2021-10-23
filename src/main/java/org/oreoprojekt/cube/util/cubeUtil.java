package org.oreoprojekt.cube.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.oreoprojekt.cube.CUBE;

import java.util.Random;

public class cubeUtil {
    private final CUBE plugin;

    public cubeUtil(CUBE plugin) {
        this.plugin = plugin;
    }

    public int getCount() {
        return plugin.ymlManager.getConfig().getInt("count");
    }

    public void printAllRoomLocation(Player player) {
        for (int rn = 0; rn < getCount(); rn++) {
            player.sendMessage(ChatColor.GRAY + "ROOM_NUMBER : " + rn + " / ROOM_X : " + plugin.ymlManager.getConfig().getInt("room." + rn + ".loc." + "locX") + " / ROOM_Z : " + plugin.ymlManager.getConfig().getInt("room." + rn + ".loc." + "locZ"));
        }
    }

    public void movePlayer(Player player, boolean hasKey) {
        int[] playerLoc = getCubePos(player);
        if (getCubeNumber(playerLoc) == -1) {
            player.sendMessage(ChatColor.RED + "ERROR_NOT_IN_ROOM");
            return;
        }
        Location block = player.getTargetBlock(3).getLocation();
        if (!(hasKey)) {
            player.sendMessage("열쇠를 소모하여 문을 엽니다.");
            player.getTargetBlock(3).setType(Material.EMERALD_BLOCK);
            generateCube(player);
        }
        switch (player.getFacing()) {
            case EAST: // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorE")) {
                    playerLoc[0] = playerLoc[0] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    player.sendMessage(getCubeNumber(playerLoc) + "번 큐브로 이동하셨습니다.");
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorW", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * 23 + 1.5), 7, (playerLoc[2] * 23 + 11.5));
                    block.add(1,0,0);
                    block.getBlock().setType(Material.EMERALD_BLOCK);
                    player.teleport(pLoc);
                }
                break;
            case NORTH: // 북쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorN")) {
                    playerLoc[2] = playerLoc[2] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    player.sendMessage(getCubeNumber(playerLoc) + "번 큐브로 이동하셨습니다.");
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorS", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * 23 + 11.5), 7, (playerLoc[2] * 23 + 21.5));
                    block.add(0,0,-1);
                    block.getBlock().setType(Material.EMERALD_BLOCK);
                    player.teleport(pLoc);
                }
                break;
            case WEST: // 서쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorW")) {
                    playerLoc[0] = playerLoc[0] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    player.sendMessage(getCubeNumber(playerLoc) + "번 큐브로 이동하셨습니다.");
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorE", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * 23 + 21.5), 7, (playerLoc[2] * 23 + 11.5));
                    block.add(-1,0,0);
                    block.getBlock().setType(Material.EMERALD_BLOCK);
                    player.teleport(pLoc);
                }
                break;
            case SOUTH: // 동쪽으로 감
                if (plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorS")) {
                    playerLoc[2] = playerLoc[2] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    player.sendMessage(getCubeNumber(playerLoc) + "번 큐브로 이동하셨습니다.");
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorN", true);
                    plugin.ymlManager.saveConfig();
                    Location pLoc = player.getLocation();
                    pLoc.set((playerLoc[0] * 23 + 11.5), 7, (playerLoc[2] * 23 + 1.5));
                    block.add(0,0,1);
                    block.getBlock().setType(Material.EMERALD_BLOCK);
                    player.teleport(pLoc);
                }
                break;
        }
    }

    public int[] getCubePos(Player player) {
        int[] playerPos = new int[3];
        int[] cubePos = new int[3];

        playerPos[0] = (int) player.getLocation().getX();
        playerPos[1] = 10;
        playerPos[2] = (int) player.getLocation().getZ();

        if (playerPos[0] < 0) {
            cubePos[0] = playerPos[0] / 23 - 1;
        }
        else {
            cubePos[0] = playerPos[0] / 23;
        }
        if (playerPos[2] < 0) {
            cubePos[2] = playerPos[2] / 23 - 1;
        }
        else {
            cubePos[2] = playerPos[2] / 23;
        }
        cubePos[1] = playerPos[1];

        return cubePos;
    } //플레이어 있는 위치를 23으로 나눈 값 리턴

    public int getCubeNumber(int[] playerLoc) {

        for (int roomNumber = 0; roomNumber < getCount(); roomNumber++) {
            if (plugin.ymlManager.getConfig().getInt("room." + roomNumber + ".loc." + "locX") == playerLoc[0] && plugin.ymlManager.getConfig().getInt("room." + roomNumber + ".loc." + "locZ") == playerLoc[2])  {
                return roomNumber;
            }
        }
        return -1;
    }

    public boolean cubeCheck(int[] playerLoc) {
        for (int roomNo = 0; roomNo < getCount(); roomNo++) {
            if (plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locX") == playerLoc[0] && plugin.ymlManager.getConfig().getInt("room." + roomNo + ".loc." + "locZ") == playerLoc[2])  {
                return true;
            }
        }
        return false;
    } //플레이어 위치에 큐브 있으면 true 없으면 false

    public void openDoor(Player player) {
        int[] playerLoc = getCubePos(player);
        if (getCubeNumber(playerLoc) == -1) {
            player.sendMessage(ChatColor.RED + "ERROR_NOT_IN_ROOM");
            return;
        }
        switch (player.getFacing()) {
            case EAST:
                if (!(plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorE"))) {
                    playerLoc[0] = playerLoc[0] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    playerLoc[0] = playerLoc[0] - 1;
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorE", true);
                    playerLoc[0] = playerLoc[0] + 1;
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorW", true);
                    plugin.ymlManager.saveConfig();
                }
                return;
            case NORTH:
                if (!(plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorN"))) {
                    playerLoc[2] = playerLoc[2] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    playerLoc[2] = playerLoc[2] + 1;
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorN", true);
                    playerLoc[2] = playerLoc[2] - 1;
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorS", true);
                    plugin.ymlManager.saveConfig();
                }
                return;
            case WEST:
                if (!(plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorW"))) {
                    playerLoc[0] = playerLoc[0] - 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    playerLoc[0] = playerLoc[0] + 1;
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorW", true);
                    playerLoc[0] = playerLoc[0] - 1;
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorE", true);
                    plugin.ymlManager.saveConfig();
                }
                return;
            case SOUTH:
                if (!(plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorS"))) {
                    playerLoc[2] = playerLoc[2] + 1; // 보고있는 방향의 방 위치값을 얻음
                    if (getCubeNumber(playerLoc) == -1) {
                        player.sendMessage(ChatColor.RED + "ERROR_ROOM_NO_EXIST");
                        return; // error
                    }
                    playerLoc[2] = playerLoc[2] - 1;
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorS", true);
                    playerLoc[2] = playerLoc[2] + 1;
                    plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorN", true);
                    plugin.ymlManager.saveConfig();
                }
        }
    }

    public boolean checkDoorOpen(Player player) { // 현재 있는 방에서 보고 있는 문이 열린문이면 true 아니면 false
        int[] playerLoc = getCubePos(player);
        switch (player.getFacing()) {
            case EAST:
                return plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorE");
            case NORTH:
                return plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorN");
            case WEST:
                return plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorW");
            case SOUTH:
                return plugin.ymlManager.getConfig().getBoolean("room." + getCubeNumber(playerLoc) + ".door." + "doorS");
        }
        return false;
    }

    public void checkMain() {
        if (getCount() > 0) return;
        genMainCube();
    }

    public void genMainCube() { // 메인 큐브 생성 (0,0)
        Bukkit.broadcastMessage(ChatColor.GREEN + "GENERATE_MAIN");
        plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locX", 0);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locY", 0);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locZ", 0);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorE", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorN", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorW", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorS", false);
        plugin.ymlManager.getConfig().set("room." + getCount() + ".type", "spawn");
        Bukkit.broadcastMessage("ROOM_COUNT_ : " + getCount());
        plugin.ymlManager.getConfig().set("count", getCount() + 1);
        plugin.ymlManager.saveConfig();
        int originX = 0;
        int originZ = 0;
        int originY = 5;
        int exX = 20000;
        int exY = 4;
        int exZ = 20000;

        Location origin = new Location(Bukkit.getWorld("world"), originX, 5, originZ);
        Location ex = new Location(Bukkit.getWorld("world"), exX, 4, exZ);

        for (int x = 0; x < 23; x++) {
            for (int z = 0; z < 23; z++) {
                for (int y = 0; y < 23; y++) {
                    origin.getBlock().setType(ex.getBlock().getType());
                    origin.set(originX + x, originY + y, originZ + z);
                    ex.set(exX + x, exY + y, exZ + z);
                }
            }
        }
        origin.getBlock().setType(Material.RED_STAINED_GLASS);
    }

    public void generateCube(Player player) {
        int[] playerLoc = getCubePos(player);
        int originX = 0;
        int originY = 5;
        int originZ = 0;

        Random random = new Random();
        int cubeShape = random.nextInt(10);

        int exX = 10000;
        int exY = 4;
        int exZ = 10000 + (23 * cubeShape);

        switch (player.getFacing()) {
            case EAST:
                plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorE", true);

                playerLoc[0] = playerLoc[0] + 1;

                if (cubeCheck(playerLoc)) {
                    player.sendMessage(ChatColor.RED + "ERROR_ROOM_ALREADY_EXISTS");
                    return;
                }

                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_EAST " + ChatColor.WHITE + getCount());
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locX", playerLoc[0]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locY", playerLoc[1]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locZ", playerLoc[2]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorE", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorN", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorW", true);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorS", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".type", "normal");
                break;
            case NORTH:
                plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorN", true);

                playerLoc[2] = playerLoc[2] - 1;

                if (cubeCheck(playerLoc)) {
                    player.sendMessage(ChatColor.RED + "ERROR_ROOM_ALREADY_EXISTS");
                    return;
                }

                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_NORTH " + ChatColor.WHITE + getCount());
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locX", playerLoc[0]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locY", playerLoc[1]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locZ", playerLoc[2]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorE", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorN", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorW", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorS", true);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".type", "normal");
                break;
            case WEST:
                plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorW", true);

                playerLoc[0] = playerLoc[0] - 1;

                if (cubeCheck(playerLoc)) {
                    player.sendMessage(ChatColor.RED + "ERROR_ROOM_ALREADY_EXISTS");
                    return;
                }

                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_WEST " + ChatColor.WHITE + getCount());
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locX", playerLoc[0]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locY", playerLoc[1]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locZ", playerLoc[2]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorE", true);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorN", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorW", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorS", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".type", "normal");
                break;
            case SOUTH:
                plugin.ymlManager.getConfig().set("room." + getCubeNumber(playerLoc) + ".door." + "doorS", true);

                playerLoc[2] = playerLoc[2] + 1;

                if (cubeCheck(playerLoc)) {
                    player.sendMessage(ChatColor.RED + "ERROR_ROOM_ALREADY_EXISTS");
                    return;
                }

                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "GENERATE_ROOM_SOUTH " + ChatColor.WHITE + getCount());
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locX", playerLoc[0]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locY", playerLoc[1]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".loc." + "locZ", playerLoc[2]);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorE", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorN", true);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorW", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".door." + "doorS", false);
                plugin.ymlManager.getConfig().set("room." + getCount() + ".type", "normal");
                break;
        }
        plugin.ymlManager.getConfig().set("count", getCount() + 1);
        plugin.ymlManager.saveConfig();

        Location origin = new Location(player.getWorld(), originX, 5, originZ);
        Location ex = new Location(player.getWorld(), exX, 4, exZ);

        if (playerLoc[0] < 0 && playerLoc[2] < 0) {  // c1 (제 3사분면)
            originX = (playerLoc[0] * 23);
            originZ = (playerLoc[2] * 23);
        }
        else if (playerLoc[0] >= 0 && playerLoc[2] < 0) { // c2 (제 2사분면)
            originX = (playerLoc[0] * 23);
            originZ = (playerLoc[2] * 23);
        }
        else if (playerLoc[0] < 0 && playerLoc[2] >= 0) { // c3 (제 4사분면)
            originX = (playerLoc[0] * 23);
            originZ = (playerLoc[2] * 23);
        }
        else { // c4 (제 1사분면)
            originX = (playerLoc[0] * 23);
            originZ = (playerLoc[2] * 23);
        }
        for (int x = 0; x <= 22; x++) {
            for (int z = 0; z <= 22; z++) {
                for (int y = 0; y <=22; y++) {
                    origin.getBlock().setType(ex.getBlock().getType());
                    origin.set(originX + x, originY + y, originZ + z);
                    ex.set(exX + x, exY + y, exZ + z);
                }
            }
        }
        origin.getBlock().setType(Material.WHITE_CONCRETE);
    }
}

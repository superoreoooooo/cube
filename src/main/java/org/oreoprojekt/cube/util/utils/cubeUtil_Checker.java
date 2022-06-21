package org.oreoprojekt.cube.util.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.util.cubeUtil;

import java.util.UUID;

public class cubeUtil_Checker {
    private CUBE plugin;
    private cubeUtil util;

    public cubeUtil_Checker(CUBE plugin) {
        this.plugin = plugin;
        this.util = new cubeUtil(plugin);
    }

    World world = cubeUtil.world;

    public int checkerTask;

    public void checkerTimer() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "checker Started!");
        checkerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    killCheckers();
                    spawnCheckers();
                }
            }
        }, 0, 20);
    }

    public void killCheckers() {
        int cnt = 0;

        for (ArmorStand checker : world.getEntitiesByClass(ArmorStand.class)) {
            if (checker.hasMetadata("pos")) {
                String[] mData = checker.getMetadata("pos").get(0).asString().split("\\.");
                if (mData[1].equals("O")) cubeUtil.OpenCheckerList.remove(checker.getUniqueId());
                else cubeUtil.ClosedCheckerList.remove(checker.getUniqueId());
                checker.remove();
                cnt++;
            }
        }

        /**
         for (Player player : Bukkit.getOnlinePlayers()) {
         for (ArmorStand checker : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
         if (checker.hasMetadata("pos")) {
         String[] mData = checker.getMetadata("pos").get(0).asString().split("\\.");
         if (!mData[0].equals(Integer.toString(util.getCubeNumber(getCubedPosition(player))))) {
         for (Player p : Bukkit.getOnlinePlayers()) {
         if (p == player) continue;
         if (mData[0].equals(Integer.toString(util.getCubeNumber(getCubedPosition(p))))) continue;
         Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "player : " + p);
         }
         if (mData[1].equals("O")) OpenCheckerList.remove(checker.getUniqueId());
         else ClosedCheckerList.remove(checker.getUniqueId());
         checker.remove();
         cnt++;
         //player.sendMessage("killed " + "R" + mData[0] + " Facing " + mData[1] + " Open? " + mData[2]);
         }
         }
         }
         }**/ //다죽이는걸로 변경됨
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "killed " + cnt + "checkers!");
    }

    public void spawnCheckers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int[] playerLoc = util.getCubedPosition(player);
            Location[] locations = new Location[4];
            if (util.getCubeNumber(playerLoc) >= 0 && util.getCubeNumber(playerLoc) <= 8) {
                //player.sendMessage("spawn");
                continue;
            }
            if (util.getCubeNumber(util.getCubedPosition(player)) == -1) continue;
            locations[0] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.roomSize + cubeUtil.roomSize - 1.5, playerLoc[1] * cubeUtil.roomSize + 1, playerLoc[2] * cubeUtil.roomSize + cubeUtil.halfRoomSize); //EAST
            locations[1] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.roomSize + cubeUtil.halfRoomSize, playerLoc[1] * cubeUtil.roomSize + 1, playerLoc[2] * cubeUtil.roomSize + 1.5); //NORTH
            locations[2] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.roomSize + 1.5, playerLoc[1] * cubeUtil.roomSize + 1, playerLoc[2] * cubeUtil.roomSize + cubeUtil.halfRoomSize); //WEST
            locations[3] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.roomSize + cubeUtil.halfRoomSize, playerLoc[1] * cubeUtil.roomSize + 1, playerLoc[2] * cubeUtil.roomSize + cubeUtil.roomSize - 1.5); //SOUTH

            for (int c = 0; c < 4; c++) {
                //player.sendMessage(ChatColor.YELLOW + "Spawning " + c + " checker");
                switch (c) {
                    case 0:
                        if (util.checkKey(player, "EAST")) spawnOpenChecker(player, locations[0], util.getCubeNumber(playerLoc) + ".E.O");
                        else spawnClosedChecker(player, locations[0], util.getCubeNumber(playerLoc) + ".E.C");
                        break;
                    case 1:
                        if (util.checkKey(player, "NORTH")) spawnOpenChecker(player, locations[1], util.getCubeNumber(playerLoc) + ".N.O");
                        else spawnClosedChecker(player, locations[1], util.getCubeNumber(playerLoc) + ".N.C");
                        break;
                    case 2:
                        if (util.checkKey(player, "WEST")) spawnOpenChecker(player, locations[2], util.getCubeNumber(playerLoc) + ".W.O");
                        else spawnClosedChecker(player, locations[2], util.getCubeNumber(playerLoc) + ".W.C");
                        break;
                    case 3:
                        if (util.checkKey(player, "SOUTH")) spawnOpenChecker(player, locations[3], util.getCubeNumber(playerLoc) + ".S.O");
                        else spawnClosedChecker(player, locations[3], util.getCubeNumber(playerLoc) + ".S.C");
                        break;
                }
            }
        }
    }

    @Deprecated
    public void killChecker(Player player) {
        int cnt = 0;
        int[] playerLoc = util.getCubedPosition(player);
        for (int i = 0; i < util.getCount(); i++) {
            if (i != util.getCubeNumber(playerLoc)) {
                for (ArmorStand checker : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
                    if (checker.hasMetadata("pos")) {
                        String[] metaData = checker.getMetadata("pos").get(0).asString().split("\\.");
                        player.sendMessage("room " + metaData[0] + " face " + metaData[1] + " O/C " + metaData[2]);
                        if (!metaData[0].equals(Integer.toString(i))) {
                            if (metaData[1].equals("O")) cubeUtil.OpenCheckerList.remove(checker.getUniqueId());
                            else cubeUtil.ClosedCheckerList.remove(checker.getUniqueId());
                            checker.remove();
                            cnt++;
                        }
                    }
                }
            }
        }
        /*
         int[] playerLoc = getCubedPosition(player);
         int cubeNo = util.getCubeNumber(playerLoc);
         int cubeNo2 = -2;
         int cnt = 0;
         //if (true) return; //난 평화주의자다! > disabled
         for (Player p : Bukkit.getOnlinePlayers()) {
         cubeNo2 = util.getCubeNumber(getCubedPosition(p));
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
         for (UUID uuid : OpenCheckerList.keySet()) {
         if (OpenCheckerList.get(uuid).equals(util.getCubeNumber(playerLoc))) {
         if (!OpenCheckerList.containsKey(uuid)) continue;
         OpenCheckerList.remove(uuid);
         player.sendMessage(uuid.toString());
         cnt++;
         }
         }
         for (UUID uuid : ClosedCheckerList.keySet()) {
         if (ClosedCheckerList.get(uuid).equals(util.getCubeNumber(playerLoc))) {
         if (!ClosedCheckerList.containsKey(uuid)) continue;
         ClosedCheckerList.remove(uuid);
         player.sendMessage(uuid.toString());
         cnt++;
         }
        */

        Bukkit.getConsoleSender().sendMessage("Killed " + cnt + " checkers!");
    } //사용안됨

    @Deprecated
    public void spawnMainChecker() {
        //spawnOpenChecker();
    } //사용안됨

    @Deprecated
    public void spawnChecker(Player player) {
        int[] playerLoc = util.getCubedPosition(player);
        Location[] locations = new Location[4];
        if (util.getCubeNumber(playerLoc) >= 0 && util.getCubeNumber(playerLoc) <= 8) {
            //player.sendMessage("spawn");
            return;
        }
        locations[0] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.roomSize + cubeUtil.roomSize -2, playerLoc[1] * cubeUtil.roomSize + 2, playerLoc[2] * cubeUtil.roomSize + cubeUtil.halfRoomSize); //EAST
        locations[1] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.roomSize + cubeUtil.halfRoomSize, playerLoc[1] * cubeUtil.roomSize + 2, playerLoc[2] * cubeUtil.roomSize + 2); //NORTH
        locations[2] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.roomSize + 2, playerLoc[1] * cubeUtil.roomSize + 2, playerLoc[2] * cubeUtil.roomSize + cubeUtil.halfRoomSize); //WEST
        locations[3] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.roomSize + cubeUtil.halfRoomSize, playerLoc[1] * cubeUtil.roomSize + 2, playerLoc[2] * cubeUtil.roomSize + cubeUtil.roomSize - 2); //SOUTH

        for (int c = 0; c < 4; c++) {
            player.sendMessage(ChatColor.YELLOW + "Spawning " + c + " checker");
            switch (c) {
                case 0:
                    if (util.checkKey(player, "EAST")) spawnOpenChecker(player, locations[0], util.getCubeNumber(playerLoc) + ".E.O");
                    else spawnClosedChecker(player, locations[0], util.getCubeNumber(playerLoc) + ".E.C");
                    break;
                case 1:
                    if (util.checkKey(player, "NORTH")) spawnOpenChecker(player, locations[1], util.getCubeNumber(playerLoc) + ".N.O");
                    else spawnClosedChecker(player, locations[1], util.getCubeNumber(playerLoc) + ".N.C");
                    break;
                case 2:
                    if (util.checkKey(player, "WEST")) spawnOpenChecker(player, locations[2], util.getCubeNumber(playerLoc) + ".W.O");
                    else spawnClosedChecker(player, locations[2], util.getCubeNumber(playerLoc) + ".W.C");
                    break;
                case 3:
                    if (util.checkKey(player, "SOUTH")) spawnOpenChecker(player, locations[3], util.getCubeNumber(playerLoc) + ".S.O");
                    else spawnClosedChecker(player, locations[3], util.getCubeNumber(playerLoc) + ".S.C");
                    break;
            }
        }
    } //사용안됨

    public void spawnOpenChecker(Player player, Location location, String checkerName) {
        location.add(0, 2.5, 0);
        ArmorStand checkerOpen = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        checkerOpen.setCustomName(ChatColor.GREEN + "OPEN");
        checkerOpen.setGravity(false);
        checkerOpen.setCanPickupItems(false);
        checkerOpen.setVisible(false);
        checkerOpen.setCanMove(false);
        checkerOpen.setMarker(true);
        checkerOpen.setCustomNameVisible(true);
        checkerOpen.setMetadata("pos", new FixedMetadataValue(plugin, checkerName));
        cubeUtil.OpenCheckerList.put(checkerOpen.getUniqueId(), util.getCubeNumber(util.getCubedPosition(player)));
    }

    public void spawnClosedChecker(Player player, Location location, String checkerName) {
        location.add(0, 2.5, 0);
        ArmorStand checkerClosed = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        checkerClosed.setCustomName(ChatColor.RED + "CLOSED");
        checkerClosed.setGravity(false);
        checkerClosed.setCanPickupItems(false);
        checkerClosed.setVisible(false);
        checkerClosed.setCanMove(false);
        checkerClosed.setMarker(true);
        checkerClosed.setCustomNameVisible(true);
        checkerClosed.setMetadata("pos", new FixedMetadataValue(plugin, checkerName));
        cubeUtil.ClosedCheckerList.put(checkerClosed.getUniqueId(), util.getCubeNumber(util.getCubedPosition(player)));
    }

    @Deprecated
    public void countChecker(Player player) {
        if (cubeUtil.OpenCheckerList.size() == 0 && cubeUtil.ClosedCheckerList.size() == 0) {
            player.sendMessage("NULL");
            return;
        }
        for (UUID uuid : cubeUtil.OpenCheckerList.keySet()) {
            if (Bukkit.getEntity(uuid) == null) return;
            player.sendMessage("O : " + uuid + cubeUtil.OpenCheckerList.get(uuid) + " DATA : " + Bukkit.getEntity(uuid).getMetadata("pos").get(0).asString());
        }
        for (UUID uuid : cubeUtil.ClosedCheckerList.keySet()) {
            if (Bukkit.getEntity(uuid) == null) return;
            player.sendMessage("C : " + uuid + cubeUtil.OpenCheckerList.get(uuid) + " DATA : " + Bukkit.getEntity(uuid).getMetadata("pos").get(0).asString());
        }
    }

    public int countCheckers() {
        int cnt = 0;
        if (cubeUtil.OpenCheckerList.size() == 0 && cubeUtil.ClosedCheckerList.size() == 0) {
            return -1;
        }
        for (UUID uuid : cubeUtil.OpenCheckerList.keySet()) {
            if (Bukkit.getEntity(uuid) != null) cnt++;
        }
        for (UUID uuid : cubeUtil.ClosedCheckerList.keySet()) {
            if (Bukkit.getEntity(uuid) != null) cnt++;
        }
        return cnt;
    }
}
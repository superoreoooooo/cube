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
import org.oreoprojekt.cube.enums.Facing;
import org.oreoprojekt.cube.util.cubeUtil;

import java.util.UUID;

public class checker {
    private final CUBE plugin;
    private final cubeUtil util;

    public checker(CUBE plugin) {
        this.plugin = plugin;
        this.util = new cubeUtil(plugin);
    }

    World world = cubeUtil.world;

    public static int checkerTask;

    private static int ct = 0;

    public void checkerTimer() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "checker Started!");
        checkerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            killCheckers();
            spawnCheckers();
        }, 0, 20);
    }

    public void killCheckers() {
        int cnt = 0;
        for (ArmorStand checker : world.getEntitiesByClass(ArmorStand.class)) {
            if (checker.hasMetadata("position")) {
                String[] mData = checker.getMetadata("position").get(0).asString().split("\\.");
                if (mData[1].equals("O")) cubeUtil.OpenCheckerList.remove(checker.getUniqueId()); // opened or closed
                else cubeUtil.ClosedCheckerList.remove(checker.getUniqueId());
                checker.remove();
                cnt++;
            }
        }
        if (cnt > 0) Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "killed " + cnt + "checkers!");
    }

    public void spawnCheckers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int[] playerLoc = util.getCubedPosition(player);
            Location[] locations = new Location[4];
            if (util.getCubeNumber(playerLoc) >= 0 && util.getCubeNumber(playerLoc) <= 8) {
                continue;
            }
            if (util.getCubeNumber(util.getCubedPosition(player)) == -1) continue;
            locations[0] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.cubeSize + cubeUtil.cubeSize - 1.5, playerLoc[1] * cubeUtil.cubeSize + 1, playerLoc[2] * cubeUtil.cubeSize + cubeUtil.halfRoomSize); //EAST
            locations[1] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.cubeSize + cubeUtil.halfRoomSize, playerLoc[1] * cubeUtil.cubeSize + 1, playerLoc[2] * cubeUtil.cubeSize + 1.5); //NORTH
            locations[2] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.cubeSize + 1.5, playerLoc[1] * cubeUtil.cubeSize + 1, playerLoc[2] * cubeUtil.cubeSize + cubeUtil.halfRoomSize); //WEST
            locations[3] = new Location(player.getWorld(), playerLoc[0] * cubeUtil.cubeSize + cubeUtil.halfRoomSize, playerLoc[1] * cubeUtil.cubeSize + 1, playerLoc[2] * cubeUtil.cubeSize + cubeUtil.cubeSize - 1.5); //SOUTH

            for (int c = 0; c < 4; c++) {
                switch (c) {
                    case 0:
                        if (util.checkKey(player, Facing.EAST)) spawnChecker(player, locations[0], util.getCubeNumber(playerLoc) + ".E.O", true);
                        else spawnChecker(player, locations[0], util.getCubeNumber(playerLoc) + ".E.C", false);
                        break;
                    case 1:
                        if (util.checkKey(player, Facing.NORTH)) spawnChecker(player, locations[1], util.getCubeNumber(playerLoc) + ".N.O", true);
                        else spawnChecker(player, locations[1], util.getCubeNumber(playerLoc) + ".N.C", false);
                        break;
                    case 2:
                        if (util.checkKey(player, Facing.WEST)) spawnChecker(player, locations[2], util.getCubeNumber(playerLoc) + ".W.O", true);
                        else spawnChecker(player, locations[2], util.getCubeNumber(playerLoc) + ".W.C", false);
                        break;
                    case 3:
                        if (util.checkKey(player, Facing.SOUTH)) spawnChecker(player, locations[3], util.getCubeNumber(playerLoc) + ".S.O", true);
                        else spawnChecker(player, locations[3], util.getCubeNumber(playerLoc) + ".S.C", false);
                        break;
                }
            }
        }
    }

    public void spawnChecker(Player player, Location location, String checkerName, Boolean isOpen) {
        location.add(0, 2.5, 0);
        ArmorStand checker = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        String op = isOpen ? ChatColor.GREEN + "OPENED" : ChatColor.RED + "CLOSED";
        checker.setCustomName(op);
        checker.setGravity(false);
        checker.setCanPickupItems(false);
        checker.setVisible(false);
        checker.setCanMove(false);
        checker.setMarker(true);
        checker.setCustomNameVisible(true);
        checker.setMetadata("position", new FixedMetadataValue(plugin, checkerName));

        if (isOpen) cubeUtil.OpenCheckerList.put(checker.getUniqueId(), util.getCubeNumber(util.getCubedPosition(player)));
        else cubeUtil.ClosedCheckerList.put(checker.getUniqueId(), util.getCubeNumber(util.getCubedPosition(player)));
    }
}
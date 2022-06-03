package org.oreoprojekt.cube.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.manager.pDataYmlManager;
import org.oreoprojekt.cube.util.cubeUtil;

public class cubeListener implements Listener {
    private CUBE plugin;
    private cubeUtil cubeUtil;
    private pDataYmlManager pDataYmlManager;

    public cubeListener(CUBE plugin) {
        this.plugin = plugin;
        this.cubeUtil = new cubeUtil(plugin);
        this.pDataYmlManager = new pDataYmlManager(plugin);
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (player.getItemInHand().getType().equals(Material.BLAZE_ROD)) {
                e.setCancelled(true);
                cubeUtil.printAllRoomLocation(player);
            }
        }
        if (player.getTargetBlock(3).getType().equals(Material.DIAMOND_BLOCK) ||
                player.getTargetBlock(3).getType().equals(Material.EMERALD_BLOCK) ||
                player.getTargetBlock(3).getType().equals(Material.GOLD_BLOCK) ||
                player.getTargetBlock(3).getType().equals(Material.NETHERITE_BLOCK)) {
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (player.getItemInHand().getType().equals(Material.IRON_HORSE_ARMOR)) {
                    cubeUtil.movePlayer(player, "normal");
                }
                else if (player.getItemInHand().getType().equals(Material.DIAMOND_HORSE_ARMOR)) {
                    cubeUtil.movePlayer(player, "master");
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        cubeUtil.restartTimer();
        pDataYmlManager.getConfig().set(e.getPlayer().getName() + ".pass", 5);
        pDataYmlManager.saveConfig();
    }
}

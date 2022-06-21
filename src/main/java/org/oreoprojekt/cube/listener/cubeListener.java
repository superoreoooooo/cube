package org.oreoprojekt.cube.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.manager.pDataYmlManager;
import org.oreoprojekt.cube.util.cubeUtil;
import org.oreoprojekt.cube.util.utils.cubeUtil_Checker;

import java.util.ArrayList;
import java.util.List;

public class cubeListener implements Listener {
    private CUBE plugin;
    private cubeUtil cubeUtil;
    private pDataYmlManager pDataYmlManager;
    private cubeUtil_Checker Util_Checker;

    private boolean timer = false;

    BukkitScheduler scheduler = Bukkit.getScheduler();
    List<Player> cooldown = new ArrayList<>();


    public cubeListener(CUBE plugin) {
        this.plugin = plugin;
        this.cubeUtil = new cubeUtil(plugin);
        this.pDataYmlManager = new pDataYmlManager(plugin);
        this.Util_Checker = new cubeUtil_Checker(plugin);
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (player.getItemInHand().getType().equals(Material.BLAZE_ROD)) {
                e.setCancelled(true);
                //cubeUtil.printAllRoomLocation(player);
                player.sendMessage("cnt : " + Util_Checker.countCheckers());
            }
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (player.getTargetBlock(3).getType().equals(Material.DIAMOND_BLOCK) || player.getTargetBlock(3).getType().equals(Material.EMERALD_BLOCK) || player.getTargetBlock(3).getType().equals(Material.GOLD_BLOCK) || player.getTargetBlock(3).getType().equals(Material.NETHERITE_BLOCK)) {
                if (player.getItemInHand().getItemMeta() == null) {
                    player.sendMessage("카드를 들고 눌러주세요.");
                    e.setCancelled(true);
                    return;
                }
                switch (player.getItemInHand().getItemMeta().getDisplayName()) {
                    case "mastercard" :
                    case "checkcard" :
                        if (cooldown.contains(player)) {
                            return;
                        }
                        cooldown.add(player);
                        itemDelay(player);
                        player.setCooldown(player.getItemInHand().getType(), 20);
                        cubeUtil.movePlayer(player);
                        e.setCancelled(true);
                        break;
                    default:
                        player.sendMessage("카드를 들고 눌러주세요.");
                        e.setCancelled(true);
                        break;
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

    @EventHandler
    public void manipulateChecker(PlayerArmorStandManipulateEvent e) {
        if (!e.getRightClicked().isVisible()) {
            e.getPlayer().sendMessage("때리지 마세요!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
    }

    public void itemDelay(Player player) {
        scheduler.runTaskLaterAsynchronously(plugin, () -> cooldown.remove(player), 20); // 쿨 1초
    }
}

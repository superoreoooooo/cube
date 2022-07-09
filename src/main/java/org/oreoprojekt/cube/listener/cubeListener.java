package org.oreoprojekt.cube.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitScheduler;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.manager.yml.pDataYmlManager;
import org.oreoprojekt.cube.util.cubeUtil;
import org.oreoprojekt.cube.util.utils.util_Checker;

import java.util.ArrayList;
import java.util.List;

public class cubeListener implements Listener {
    private final CUBE plugin;
    private final cubeUtil cubeUtil;
    private final pDataYmlManager pDataYmlManager;
    private final util_Checker Util_Checker;

    BukkitScheduler scheduler = Bukkit.getScheduler();
    List<Player> coolDown = new ArrayList<>();

    public cubeListener(CUBE plugin) {
        this.plugin = plugin;
        this.cubeUtil = new cubeUtil(plugin);
        this.pDataYmlManager = new pDataYmlManager(plugin);
        this.Util_Checker = new util_Checker(plugin);
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) {
                e.setCancelled(true);
                cubeUtil.printPlayerLoc(player);
            }
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (player.getTargetBlock(3).getType().equals(Material.DIAMOND_BLOCK) || player.getTargetBlock(3).getType().equals(Material.EMERALD_BLOCK) || player.getTargetBlock(3).getType().equals(Material.GOLD_BLOCK) || player.getTargetBlock(3).getType().equals(Material.NETHERITE_BLOCK)) {
                if (player.getInventory().getItemInMainHand().getItemMeta() == null) {
                    player.sendMessage("카드를 들고 눌러주세요.");
                    e.setCancelled(true);
                    return;
                }
                if (player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE)) {
                    player.sendMessage("난 무적이다");
                    return;
                }
                switch (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName()) {
                    case "mastercard" :
                    case "checkcard" :
                        if (coolDown.contains(player)) {
                            return;
                        }
                        coolDown.add(player);
                        itemDelay(player);
                        player.setCooldown(player.getInventory().getItemInMainHand().getType(), 20);
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
        Util_Checker.checkerTimer();
    }

    @EventHandler
    public void manipulateChecker(PlayerArmorStandManipulateEvent e) {
        if (!e.getRightClicked().isVisible()) {
            e.getPlayer().sendMessage("때리지 마세요!");
            e.setCancelled(true);
        }
    }

    public void itemDelay(Player player) {
        scheduler.runTaskLaterAsynchronously(plugin, () -> coolDown.remove(player), 20); // 쿨 1초
    }
}

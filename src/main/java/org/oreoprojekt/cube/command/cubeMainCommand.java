package org.oreoprojekt.cube.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.oreoprojekt.cube.CUBE;
import org.oreoprojekt.cube.util.cubeUtil;

import java.util.ArrayList;
import java.util.List;

public class cubeMainCommand implements CommandExecutor {
    public CUBE plugin;
    private final cubeUtil cubeUtils;

    public cubeMainCommand(CUBE plugin) {
        this.plugin = plugin;
        this.cubeUtils = new cubeUtil(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("/cube stop, start, reset, save, reload, item, charge");
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stop")) {
                player.sendMessage(ChatColor.RED + plugin.getConfig().getString("message.stop"));
                cubeUtils.restartTimer();
                return true;
            }
            if (args[0].equalsIgnoreCase("start")) {
                player.sendMessage(ChatColor.RED + plugin.getConfig().getString("message.start"));
                cubeUtils.startTimer();
                return true;
            }
            if (args[0].equalsIgnoreCase("reset")) {
                player.sendMessage(ChatColor.RED + plugin.getConfig().getString("message.reset"));
                plugin.saveDefaultConfig();
                return true;
            }
            if (args[0].equalsIgnoreCase("save")) {
                player.sendMessage(ChatColor.RED + plugin.getConfig().getString("message.save"));
                plugin.saveConfig();
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                player.sendMessage(ChatColor.RED + plugin.getConfig().getString("message.reload"));
                plugin.reloadConfig();
                return true;
            }
            if (args[0].equalsIgnoreCase("charge")) {
                plugin.pDataYmlManager.getConfig().set(player.getName() + ".pass", 1);
                plugin.pDataYmlManager.saveConfig();
                return true;
            }
            if (args[0].equalsIgnoreCase("mute")) {
                return true;
            }
            if (args[0].equalsIgnoreCase("item")) {
                player.sendMessage(ChatColor.GOLD + plugin.getConfig().getString("message.getItem"));
                ItemStack item1 = new ItemStack(Material.IRON_HORSE_ARMOR);
                ItemStack item2 = new ItemStack(Material.BLAZE_ROD);
                ItemStack item3 = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
                ItemMeta meta1 = item1.getItemMeta();
                ItemMeta meta2 = item2.getItemMeta();
                ItemMeta meta3 = item3.getItemMeta();
                List<String> lore1 = new ArrayList<>();
                List<String> lore2 = new ArrayList<>();
                List<String> lore3 = new ArrayList<>();
                lore1.add("우클릭 : 현재 보고 있는 문을 열음(시도)");
                lore2.add("우클릭 : 생성된 모든 방과 위치 확인");
                lore3.add("마스터카드 파시블?");
                meta1.setDisplayName("checkcard");
                meta2.setDisplayName("CUBE_CHECKER");
                meta3.setDisplayName("mastercard");
                meta1.setLore(lore1);
                meta2.setLore(lore2);
                meta3.setLore(lore3);
                item1.setItemMeta(meta1);
                item2.setItemMeta(meta2);
                item3.setItemMeta(meta3);
                player.getInventory().addItem(item1);
                player.getInventory().addItem(item2);
                player.getInventory().addItem(item3);
                return true;
            }
            else {
                player.sendMessage("error");
            }
        }
        return false;
    }
}

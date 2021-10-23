package org.oreoprojekt.cube.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class cubeItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        player.sendMessage(ChatColor.GOLD + "아이템을 받으셨습니다.");
        ItemStack item1 = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack item2 = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta1 = item1.getItemMeta();
        ItemMeta meta2 = item2.getItemMeta();
        List<String> lore1 = new ArrayList<String>();
        List<String> lore2 = new ArrayList<String>();
        lore1.add("우클릭 : 현재 보고 있는 문의 열림/잠김 상태 확인");
        lore1.add("좌클릭 : 잠긴 문을 열음");
        lore2.add("우클릭 : 보고 있는 방향으로 새로운 문 생성");
        lore2.add("좌클릭 : 생성된 모든 방과 위치 확인");
        meta1.setDisplayName("CUBE_1");
        meta2.setDisplayName("CUBE_2");
        meta1.setLore(lore1);
        meta2.setLore(lore2);
        item1.setItemMeta(meta1);
        item2.setItemMeta(meta2);
        player.getInventory().addItem(item1);
        player.getInventory().addItem(item2);
        return false;
    }
}

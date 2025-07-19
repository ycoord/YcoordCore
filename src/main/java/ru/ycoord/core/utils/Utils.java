package ru.ycoord.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Utils {
    public static ItemStack createPlayerHead(String playerName){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(playerName);
        meta.setOwningPlayer(offlinePlayer); // привязка головы к игроку
        head.setItemMeta(meta);

        return head;
    }

    public static ItemStack createPlayerHead(UUID playerUUID){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        meta.setOwningPlayer(offlinePlayer); // привязка головы к игроку
        head.setItemMeta(meta);

        return head;
    }

    public static CompletableFuture<ItemStack> createPlayerHeadAsync(String playerName){
        return CompletableFuture.supplyAsync(()-> Utils.createPlayerHead(playerName));
    }
    public static CompletableFuture<ItemStack> createPlayerHeadAsync(UUID uuid){
        return CompletableFuture.supplyAsync(()-> Utils.createPlayerHead(uuid));
    }
}

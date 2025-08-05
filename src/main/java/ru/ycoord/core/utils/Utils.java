package ru.ycoord.core.utils;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Utils {
    public static ItemStack createPlayerHead(String playerName) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(playerName);
        meta.setOwningPlayer(offlinePlayer); // привязка головы к игроку
        head.setItemMeta(meta);

        return head;
    }

    public static ItemStack createPlayerHead(UUID playerUUID) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        meta.setOwningPlayer(offlinePlayer); // привязка головы к игроку
        head.setItemMeta(meta);

        return head;
    }

    public static ItemStack createPlayerHeadBase64(String base64) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        head.setItemMeta(meta);
        return head;
    }

    public static CompletableFuture<ItemStack> createPlayerHeadAsync(String playerName) {
        return CompletableFuture.supplyAsync(() -> Utils.createPlayerHead(playerName));
    }

    public static CompletableFuture<ItemStack> createPlayerHeadAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> Utils.createPlayerHead(uuid));
    }

    public static CompletableFuture<ItemStack> createPlayerHeadBase64Async(String base64) {
        return CompletableFuture.supplyAsync(() -> Utils.createPlayerHeadBase64(base64));
    }

    public static void executeConsole(Player player, String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, command.replace("%player%", player.getName())));
    }

    public static void executePlayer(Player player, String command) {
        Bukkit.dispatchCommand(player, PlaceholderAPI.setPlaceholders(player, command.replace("%player%", player.getName())));
    }

    public static void executeConsole(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static boolean checkCondition(Player player, String condition, MessagePlaceholders placeholders) {
        String parsed = MessageBase.translateColor(condition, placeholders);
        Expression expression = new Expression(parsed);
        try {
            EvaluationValue result = expression.evaluate();
            return result.getBooleanValue();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return false;
    }

    public static String convertTime(Long milli) {
        return convertTime(milli, "HH:mm:ss dd-MM-yyyy");
    }

    public static String convertTime(Long milli, String pattern) {
        Date currentDate = new Date(milli);
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(currentDate);
    }
}

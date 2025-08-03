package ru.ycoord.core.gui.items;


import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.ycoord.YcoordCore;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.gui.GuiManager;
import ru.ycoord.core.messages.ChatMessage;
import ru.ycoord.core.messages.MessageBase;
import ru.ycoord.core.messages.MessagePlaceholders;
import ru.ycoord.core.sound.SoundInfo;
import ru.ycoord.core.utils.Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GuiItem {
    protected final ConfigurationSection section;
    private SoundInfo sound = null;
    private List<String> consoleCommands = new LinkedList<>();
    private List<String> playerCommands = new LinkedList<>();
    private List<String> lore = new LinkedList<>();
    private String consolePermission = "*";
    private String playerPermission = "*";
    private String consoleNoPermission = null;
    private String playerNoPermission = null;
    protected final long current = System.currentTimeMillis();

    public GuiItem(ConfigurationSection section) {
        this.section = section;
        this.lore = section.getStringList("lore");
        ConfigurationSection soundSection = section.getConfigurationSection("sound");

        if (soundSection != null) {
            this.sound = new SoundInfo(soundSection);
        } else {
            ConfigurationSection config = YcoordCore.getInstance().getConfig();
            ConfigurationSection itemSettings = config.getConfigurationSection("gui-settings");
            if (itemSettings == null)
                return;

            ConfigurationSection soundSettings = itemSettings.getConfigurationSection("item-sound");
            if (soundSettings == null)
                return;


            boolean useDefaultSound = soundSettings.getBoolean("use-default", true);
            if (useDefaultSound) {
                ConfigurationSection defaultSoundSection = soundSettings.getConfigurationSection("default-sound");
                if (defaultSoundSection == null)
                    return;
                this.sound = new SoundInfo(defaultSoundSection);
            }
        }

        if (section.contains("commands.player"))
            this.playerCommands = section.getStringList("commands.player");
        if (section.contains("commands.console"))
            this.consoleCommands = section.getStringList("commands.console");
        if (section.contains("commands.console-permission"))
            this.consolePermission = section.getString("console-permission");
        if (section.contains("commands.player-permission"))
            this.playerPermission = section.getString("player-permission");
        if (section.contains("commands.player-no-permission-message"))
            this.playerNoPermission = section.getString("commands.player-no-permission-message");
        if (section.contains("commands.console-no-permission-message"))
            this.consoleNoPermission = section.getString("commands.console-no-permission-message");
    }

    protected List<String> getLoreBefore(OfflinePlayer player) {
        return new LinkedList<>();
    }

    protected List<String> getLoreAfter(OfflinePlayer player) {
        return new LinkedList<>();
    }

    public void apply(OfflinePlayer clicker, ItemStack stack, MessagePlaceholders placeholders) {
        if (clicker.getPlayer() != null)
            placeholders.put("%player%", clicker.getPlayer().getName());
        List<String> loreBefore = getLoreBefore(clicker);

        List<String> loreAfter = getLoreAfter(clicker);

        List<String> resultLore = new LinkedList<>();
        for (String loreItem : loreBefore) {
            resultLore.add(MessageBase.translateColor(loreItem, placeholders));
        }

        for (String loreItem : lore) {
            resultLore.add(MessageBase.translateColor(loreItem, placeholders));
        }

        for (String loreItem : loreAfter) {
            resultLore.add(MessageBase.translateColor(loreItem, placeholders));
        }

        ItemMeta meta = stack.getItemMeta();
        String name = section.getString("name", "имя");
        meta.displayName(Component.text(MessageBase.translateColor(name, placeholders)));

        List<Component> components = new LinkedList<>();
        for (String loreItem : resultLore) {
            meta.lore(new LinkedList<>());
            components.add(Component.text(MessageBase.translateColor(loreItem, placeholders)));
        }

        meta.lore(components);

        meta.setCustomModelData(999);
        stack.setItemMeta(meta);

        if (section.getBoolean("glow", false)) {
            stack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
            ItemMeta meta2 = stack.getItemMeta();
            meta2.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            stack.setItemMeta(meta2);
        }

        stack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS);
    }

    public ItemStack buildItem(OfflinePlayer clicker, GuiBase base, int slot, MessagePlaceholders placeholders) {
        ItemStack stack;

        String texture = section.getString("texture", null);
        if (texture != null && !texture.isEmpty()) {
            Utils.createPlayerHeadBase64Async(texture).thenAccept(item -> {
                Bukkit.getScheduler().runTask(YcoordCore.getInstance(), () -> {
                    apply(clicker, item, placeholders);
                    base.getInventory().setItem(slot, item);
                });
            });
            stack = new ItemStack(Material.PLAYER_HEAD);

        } else {
            stack = new ItemStack(Material.valueOf(section.getString("material", "BARRIER")));
        }

        apply(clicker, stack, placeholders);

        return stack;
    }

    private boolean checkCooldown(Player clicker) {
        HashMap<String, Long> cd = GuiManager.cooldowns;
        {
            long curr = System.currentTimeMillis();
            if (!cd.containsKey(clicker.getName())) {
                cd.put(clicker.getName(), curr);
                return true;
            } else {
                Long lastClick = cd.get(clicker.getName());
                cd.put(clicker.getName(), curr);
                long diff = curr - lastClick;
                if (diff >= GuiManager.cooldown) {
                    return true;
                }

                YcoordCore.getInstance().getChatMessage().sendMessageId(clicker, "messages.cooldown", new MessagePlaceholders(clicker));

            }
        }
        return false;
    }


    public boolean handleClick(GuiBase gui, InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player clicker) {
            if(!checkCooldown(clicker))
                return false;
            playSound(clicker);
            runCommands(clicker, event.getSlot());
        }

        return true;
    }

    protected void playSound(Player player) {
        if (this.sound != null) {
            this.sound.play(player);
        }
    }

    protected void runCommands(Player player, int slot) {
        MessagePlaceholders messagePlaceholders = new MessagePlaceholders(player);
        messagePlaceholders.put("%slot%", slot);
        if (playerPermission == null || player.hasPermission(playerPermission)) {
            for (String command : playerCommands) {
                Utils.executePlayer(player, messagePlaceholders.apply(command));
            }
        } else {
            ChatMessage chatMessage = YcoordCore.getInstance().getChatMessage();
            chatMessage.sendMessageId(player, playerNoPermission, "", messagePlaceholders);
        }

        if (consolePermission == null || player.hasPermission(consolePermission)) {
            for (String command : consoleCommands) {
                Utils.executeConsole(player, messagePlaceholders.apply(command));
            }
        } else {
            ChatMessage chatMessage = YcoordCore.getInstance().getChatMessage();
            chatMessage.sendMessageId(player, consoleNoPermission, "", messagePlaceholders);
        }
    }

    public void update(GuiBase guiBase, long elapsed, Player player) {

    }
}

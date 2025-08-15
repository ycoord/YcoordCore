package ru.ycoord.core.gui.items;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.ycoord.core.gui.GuiBase;
import ru.ycoord.core.messages.MessagePlaceholders;

import java.util.List;

public abstract class GuiMultiItem extends GuiItem {
    public GuiMultiItem(int priority, int slot, int index, @Nullable ConfigurationSection section) {
        super(priority, slot, index, section);

    }

    @Override
    public ItemStack buildItem(OfflinePlayer clicker, GuiBase base, int slot, int index, MessagePlaceholders placeholders, boolean onlyMeta) {
        return getCurrentSlot(clicker).buildItem(clicker, base, slot, index, placeholders, onlyMeta);
    }



    @Override
    public List<String> getLoreBefore(OfflinePlayer ignored) {
        return getCurrentSlot(ignored).getLoreBefore(ignored);
    }

    @Override
    public List<String> getLoreAfter(OfflinePlayer ignored) {
        return getCurrentSlot(ignored).getLoreAfter(ignored);
    }

    @Override
    public void apply(OfflinePlayer clicker, ItemStack stack, MessagePlaceholders placeholders) {
        getCurrentSlot(clicker).apply(clicker, stack, placeholders);
    }

    @Override
    public void update(GuiBase guiBase, int slot, int index, long elapsed, Player player, MessagePlaceholders placeholders) {
        getCurrentSlot(player).update(guiBase, slot, index, elapsed, player, placeholders);
    }

    @Override
    public ItemStack createItem(OfflinePlayer player, GuiBase base, int slot) {
        return getCurrentSlot(player).createItem(player, base, slot);
    }

    @Override
    public boolean checkCooldown(Player clicker) {
        return getCurrentSlot(clicker).checkCooldown(clicker);
    }


    @Override
    public boolean checkCondition(Player player, MessagePlaceholders placeholders) {
        return getCurrentSlot(player).checkCondition(player, placeholders);
    }

    @Override
    public boolean handleClick(GuiBase gui, InventoryClickEvent event, MessagePlaceholders placeholders) {
        return getCurrentSlot((Player) event.getWhoClicked()).handleClick(gui, event, placeholders);
    }

    @Override
    public boolean handleClick(GuiBase gui, boolean left, Player player, MessagePlaceholders placeholders) {
        return getCurrentSlot(player).handleClick(gui, left, player, placeholders);
    }

    @Override
    public void handleCondition(GuiBase guiBase, int slot, int index, Player player, MessagePlaceholders messagePlaceholders) {
        getCurrentSlot(player).handleCondition(guiBase, slot, index, player, messagePlaceholders);
    }

    @Override
    public void getExtraPlaceholders(OfflinePlayer player, MessagePlaceholders placeholders, int slot, int index, GuiBase base) {
        getCurrentSlot(player).getExtraPlaceholders(player, placeholders, slot, index, base);
    }

    public abstract GuiItem getCurrentSlot(OfflinePlayer player);
}

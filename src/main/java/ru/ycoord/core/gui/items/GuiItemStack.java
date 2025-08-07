package ru.ycoord.core.gui.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import ru.ycoord.core.gui.GuiBase;

public class GuiItemStack extends GuiItem {
    private final ItemStack item;

    public GuiItemStack(ItemStack item, int priority, int slot, int index, ConfigurationSection section) {
        super(priority, slot, index, section);
        this.item = item;
    }

    @Override
    protected ItemStack createItem(GuiBase base, int slot) {
        return item.clone();
    }
}

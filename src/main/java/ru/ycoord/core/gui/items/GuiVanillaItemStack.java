package ru.ycoord.core.gui.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class GuiVanillaItemStack extends GuiItemStack{
    public GuiVanillaItemStack(ItemStack item, int priority, int slot, int index) {
        super(item, priority, slot, index, null);
    }
}

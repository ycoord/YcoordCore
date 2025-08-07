package ru.ycoord.core.gui.items;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class GuiViewerHeadItem extends GuiHeadItem {

    public GuiViewerHeadItem(@NotNull String name, int priority, int slot, int index, ConfigurationSection section) {
        super(name, priority, slot, index, section);
    }
}

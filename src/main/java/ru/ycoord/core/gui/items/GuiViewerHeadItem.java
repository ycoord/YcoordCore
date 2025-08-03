package ru.ycoord.core.gui.items;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class GuiViewerHeadItem extends GuiHeadItem {

    public GuiViewerHeadItem(@NotNull String name, int priority, ConfigurationSection section) {
        super(name, priority, section);
    }
}
